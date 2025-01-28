import json
import re

from logger import logger
from datetime import datetime, timedelta, timezone
import secrets
from fastapi import FastAPI, HTTPException, Depends, status, Request, UploadFile, File, WebSocket, WebSocketDisconnect, BackgroundTasks, Response
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from fastapi.responses import JSONResponse
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import func
from sqlalchemy.future import select

from database.db_config import SessionLocal
from database.models.user import User
from database.models.blacklisted_tokens import BlacklistedTokens
from database.models.current_online_users import CurrentOnlineUsers
from database.models.user_observations import UserObservation
from database.models.user_observations_encrypted import UserObservationsEncrypted

from schemas.user import UserCreate, UserDataResponse, UpdateUserRequest
from schemas.prediction_request import PredictionRequest
from schemas.encrypted_data import EncryptedData, PasswordRequest
from passlib.context import CryptContext
from pathlib import Path
import jwt
import logging
import shutil
from passlib.hash import bcrypt
from starlette.middleware.cors import CORSMiddleware
from apscheduler.schedulers.asyncio import AsyncIOScheduler
from contextlib import asynccontextmanager
from ML.models.execute_model import execute_model
from fastapi.responses import FileResponse
import os
import websockets
import aiofiles
from typing import Set, List, Dict
import pandas as pd
import base64
from redis.asyncio import Redis
from io import StringIO
from dotenv import load_dotenv
import asyncio
from FL_scripts.aggregator import create_global_checkpoint
import random

# Load environment variables from .env file
load_dotenv()


# JWT settings
SECRET_KEY = os.getenv("SECRET_KEY")
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 30  # Token expiration time in minutes

# Password hashing
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

redis_client = None





app = FastAPI()


# Initialize the scheduler
# scheduler = AsyncIOScheduler()
@asynccontextmanager
async def lifespan(app: FastAPI):
    print("Application starting...")
    global redis_client
    redis_client = await Redis.from_url("redis://localhost", encoding="utf-8", decode_responses=True)
    async with SessionLocal() as db:
        # Using select to get usernames and emails
        result = await db.execute(select(User.username, User.email))
        users = result.all()

        for username, email in users:
            await redis_client.sadd("registered_usernames", username)
            await redis_client.sadd("registered_emails", email)
    await redis_client.set('global_FL_round', str(get_latest_round()))
    asyncio.create_task(listen_for_messages())
    #scheduler.start()
    yield
    await redis_client.flushall()
    #scheduler.shutdown()
    print("Application shutting down...")






app = FastAPI(lifespan=lifespan)




# Configure CORS, security measure to prevent malicious scripts from making unauthorized requests
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], # Allow requests from any origin (change as needed) e.g. localhost:3000, some other ip-address etc
    allow_credentials=True,
    allow_methods=["*"],  # Allow methods from any origin (change as needed) e.g. GET,POST,PUT etc
    allow_headers=["*"],  # Allow headers from any origin (change as needed) e.g. Content-Type: application/x-www-form-urlencoded
)




oauth2_scheme = OAuth2PasswordBearer(tokenUrl="login")

logging.disable(logging.INFO)




# Dependency to get the async database session
async def get_db():
    async with SessionLocal() as session:
        yield session


# Function to verify password
def verify_password(plain_password, hashed_password):
    return pwd_context.verify(plain_password, hashed_password)

# Function to create access token
def create_access_token(data: dict):
    to_encode = data.copy()
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)



async def get_current_user(token: str = Depends(oauth2_scheme)):
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        if username is None:
            raise HTTPException(status_code=401, detail="Could not validate credentials")
        return username
    except jwt.PyJWTError:
        raise HTTPException(status_code=401, detail="Could not validate credentials")


async def get_current_user_role(token: str = Depends(oauth2_scheme)):
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        role: str = payload.get("role")
        if role is None:
            raise HTTPException(status_code=401, detail="Could not validate credentials")
        return role
    except jwt.PyJWTError:
        raise HTTPException(status_code=401, detail="Could not validate credentials")




async def check_token_in_blacklist(token: str) -> bool:
    # Check if the token exists in the "blacklisted_tokens" hash in Redis
    exists = await redis_client.hexists("blacklisted_tokens", token)
    return bool(exists)

async def verify_token_not_blacklisted(request: Request, token: str = Depends(oauth2_scheme)) -> None:
    if await check_token_in_blacklist(token):
        logger.critical(f"The following IP Address: {request.client.host} tried to access an endpoint using this blacklisted token: {token}")
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token is blacklisted",
            headers={"WWW-Authenticate": "Bearer"},
        )



# Endpoint to register a new user
@app.post("/register", tags=["General Post Methods"])
async def register_user(user: UserCreate, db: AsyncSession = Depends(get_db)):
    # Check if username already exists
    if await redis_client.sismember("registered_usernames", user.username):
        raise HTTPException(status_code=400, detail="Username is already in use")

    # Check if email already exists
    if await redis_client.sismember("registered_emails", user.email):
        raise HTTPException(status_code=400, detail="Email is already in use")

    # If username and email are unique, proceed to create the user
    hashed_password = pwd_context.hash(user.password)
    salt = base64.urlsafe_b64encode(os.urandom(32)).decode('utf-8')
    db_user = User(username=user.username, password=hashed_password, email=user.email, role="user", access_type="free", salt=salt)
    db.add(db_user)
    await db.commit()
    await redis_client.sadd("registered_usernames", user.username)
    await redis_client.sadd("registered_emails", user.email)
    return {"Message": f"User {user.username} successfully registered"}

# Endpoint to login and generate JWT token
@app.post("/login", tags=["General Post Methods"])
async def login_access_token(request: Request, form_data: OAuth2PasswordRequestForm = Depends(), db: AsyncSession = Depends(get_db), ):

    # Check if the user is already online
    if await redis_client.sismember("online_users", form_data.username):
        raise HTTPException(status_code=400, detail="User already online")

    # Query database to find user by username
    db_user = await db.execute(select(User).filter(User.username == form_data.username))
    user = db_user.scalar()

    # Verify user exists and password matches
    if not user or not verify_password(form_data.password, user.password):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Incorrect username or password")


    # Add user to online users
    await redis_client.sadd("online_users", form_data.username)



    # Create JWT token with user's data
    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token({"sub": user.username, "role": user.role, "exp": datetime.now(timezone.utc) + access_token_expires})
    logger.info(f"User {user.username} logged in at {datetime.now()} from the IP {request.client.host}")
    return {"access_token": access_token, "token_type": "bearer"}

# Endpoint to logout and blacklist token
@app.post("/logout", tags=["General Post Methods"])
async def logout_user(token: str = Depends(oauth2_scheme)):

    if token is None:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="No token provided")

    # Insert blacklisted token to list
    await redis_client.hset("blacklisted_tokens", token, datetime.now(timezone.utc).isoformat())

    # Remove the user from the current_online_users table
    current_user_username = await get_current_user(token)
    await redis_client.srem("online_users", current_user_username)

    logger.info(f"User {current_user_username} logged out.")
    return {"Message": "Logout Successful"}




async def get_max_observation_id(db: AsyncSession, user_id: int) -> int:
    result = await db.execute(
        select(func.max(UserObservation.observation_id)).where(UserObservation.user_id == user_id)
    )
    max_observation_id = result.scalar() or 0  # If no records found, start from 0
    return max_observation_id



async def get_user_id_by_username(username: str, db: AsyncSession) -> int:
    query = select(User.id).filter(User.username == username)
    result = await db.execute(query)
    user_id = result.scalar_one_or_none()

    if user_id is None:
        raise ValueError(f"User with username '{username}' not found")

    return user_id


async def get_user_id_and_max_observation_id(username: str, db: AsyncSession):
    query = (
        select(User.id, func.coalesce(func.max(UserObservation.observation_id), 0))
        .join(UserObservation, User.id == UserObservation.user_id, isouter=True)
        .where(User.username == username)
        .group_by(User.id)
    )
    result = await db.execute(query)
    user_id, max_observation_id = result.one()
    return user_id, max_observation_id


async def insert_observations_chunk(chunk, current_user_username: str, db: AsyncSession):
    if chunk.empty:
        return

    user_id, max_observation_id = await get_user_id_and_max_observation_id(current_user_username, db)
    current_observation_id = max_observation_id + 1

    observations = []
    for index, row in chunk.iterrows():
        date_str = row['date']
        date_obj = datetime.fromisoformat(date_str)

        observation = UserObservation(
            user_id=user_id,
            observation_id=current_observation_id,
            feature1=row['feature1'],
            feature2=row['feature2'],
            feature3=row['feature3'],
            feature4=row['feature4'],
            date=date_obj
        )
        observations.append(observation)
        current_observation_id += 1

    try:
        db.add_all(observations)
        await db.commit()
    except Exception as e:
        await db.rollback()
        raise HTTPException(status_code=500, detail=f"Error inserting observations: {str(e)}")


async def process_csv(file_path: str, current_user_username: str, db: AsyncSession, chunk_size: int = 10000):
    try:
        async with aiofiles.open(file_path, mode='r') as f:
            content = await f.read()  # Read the entire content
            # Use StringIO to convert the string into a file-like object for pandas
            reader = pd.read_csv(StringIO(content), chunksize=chunk_size)
            for chunk in reader:
                await insert_observations_chunk(chunk, current_user_username, db)
    except Exception as e:
        logger.error(f"Error processing CSV: {e}")
    finally:
        os.remove(file_path)


async def save_file(upload_file: UploadFile, destination: Path):
    try:
        async with aiofiles.open(destination, 'wb') as out_file:
            while content := await upload_file.read(1024):
                await out_file.write(content)
    except Exception as e:
        logger.error(f"Error saving file {upload_file.filename}: {str(e)}")
        raise HTTPException(status_code=500, detail="Internal Server Error")






# Endpoint for file upload, files are uploaded to each user's folder
@app.post("/upload", tags=["Upload Methods"])
async def upload_file_route(
        request: Request,
        background_tasks: BackgroundTasks,
        files: list[UploadFile] = File(...),
        token: str = Depends(oauth2_scheme),
        db: AsyncSession = Depends(get_db)

):
    await verify_token_not_blacklisted(request, token)
    current_user_username = await get_current_user(token)

    allowed_extensions = {'.zip', '.csv'}
    user_folder = Path(f"uploads/{current_user_username}")
    user_folder.mkdir(parents=True, exist_ok=True)

    # List to keep track of processed files
    uploaded_files_info = []

    for file in files:
        file_extension = Path(file.filename).suffix.lower()
        if file_extension not in allowed_extensions:
            raise HTTPException(status_code=400, detail=f"Only {', '.join(allowed_extensions)} files are allowed")

        file_location = user_folder / file.filename
        await save_file(file, file_location)

        if file_extension == ".csv":
            background_tasks.add_task(process_csv, str(file_location), current_user_username, db)
            logger.info(
                f"{current_user_username} successfully uploaded and started processing the file {file.filename}")
            uploaded_files_info.append(f"file '{file.filename}' saved at '{file_location}' and processing started")
        elif file_extension == ".zip":
            logger.info(
                f"{current_user_username} successfully uploaded the file {file.filename} at the directory {file_location}")
            uploaded_files_info.append(f"file '{file.filename}' saved at '{file_location}'")

    return {"message": "Data was successfully inserted and/or files were successfully uploaded",
            "files": uploaded_files_info}


# Prediction endpoint, with model parameter
@app.post("/make_prediction/{model}", tags=["Predict Post Methods"])
async def make_prediction(request: Request, predictionrequest: PredictionRequest, model: str, token: str = Depends(oauth2_scheme)):
    await verify_token_not_blacklisted(request, token)
    return execute_model(model, predictionrequest)


# Endpoint to check token validity
@app.post("/verifyToken")
async def verify_token(request: Request, token: str = Depends(oauth2_scheme)):
    # Verify the token is not blacklisted
    await verify_token_not_blacklisted(request, token)

    # Get the current user from the token
    current_user = await get_current_user(token)  # Ensure get_current_user is async

    if not current_user:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Token is not valid")

    return {"Message": "Token is valid"}


@app.post("/download-onnx-file")
async def download_onnx_file(request: Request, token: str = Depends(oauth2_scheme)):
    # Verify the token is not blacklisted
    await verify_token_not_blacklisted(request, token)

    file_path = "ML/model.onnx"
    if os.path.exists(file_path):
        return FileResponse(path=file_path, filename="model.onnx", media_type='application/octet-stream')
    else:
        return {"error": "File not found"}


@app.post("/upload-onnx-file")
async def upload_onnx_file(request: Request, token: str = Depends(oauth2_scheme), file: UploadFile = File(...)):
    # Verify the token is not blacklisted
    await verify_token_not_blacklisted(request, token)
    upload_folder = Path("uploads/")
    upload_folder.mkdir(parents=True, exist_ok=True)
    file_path = upload_folder / file.filename
    with open(file_path, "wb") as f:
        shutil.copyfileobj(file.file, f)
    current_user = await get_current_user(token)

    logger.info(f"User '{current_user}' successfully uploaded the file '{file.filename}' to '/uploads'. User IP Address: {request.client.host}. User Token: '{token}'")

    return {"Message": "The .onnx file was successfully uploaded"}


@app.post("/refreshToken")
async def refresh_token(request: Request, token: str = Depends(oauth2_scheme)):
    # Verify the token is not blacklisted
    await verify_token_not_blacklisted(request, token)

    # Insert blacklisted token to list
    await redis_client.hset("blacklisted_tokens", token, datetime.now(timezone.utc).isoformat())

    # Extract current user data from the token
    current_username = await get_current_user(token)

    # Create JWT token with user's data
    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        {"sub": current_username, "role": "user", "exp": datetime.now(timezone.utc) + access_token_expires})
    logger.info(f"User {current_username} refreshed JWT Token")
    return {"access_token": access_token, "token_type": "bearer"}


@app.post("/get_user_data")
async def get_user_data(request: Request, token: str = Depends(oauth2_scheme), db: AsyncSession = Depends(get_db) ):
    # Verify the token is not blacklisted
    await verify_token_not_blacklisted(request, token)
    username = await get_current_user(token)

    result = await db.execute(select(User).filter(User.username == username))
    user = result.scalar_one_or_none()
    if not user:
        raise HTTPException(status_code=404, detail="User was not found")

    return UserDataResponse(
        username=user.username,
        email=user.email,
        access_type=user.access_type,
        federated_learning=user.federated_learning,
        sharing4good=user.sharing4good
    )


@app.post("/update_user_preferences/{preference_type}/{boolean_preference}")
async def update_user_preferences(preference_type: str, boolean_preference: bool, request: Request, token: str = Depends(oauth2_scheme), db: AsyncSession = Depends(get_db) ):
    # Verify the token is not blacklisted
    await verify_token_not_blacklisted(request, token)
    username = await get_current_user(token)
    # Fetch user from database based on username
    result = await db.execute(select(User).filter(User.username == username))
    user = result.scalar_one_or_none()

    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    # Update user preferences based on preference_type
    if preference_type == "federated_learning":
        user.federated_learning = boolean_preference
    elif preference_type == "sharing4good":
        user.sharing4good = boolean_preference
    else:
        raise HTTPException(status_code=400, detail="Invalid preference type")

    # Commit changes to database
    await db.commit()

    return {"message": f"{preference_type.capitalize()} preference updated successfully."}



@app.post("/update_user_info")
async def update_user_info(update_request: UpdateUserRequest, request: Request, token: str = Depends(oauth2_scheme), db: AsyncSession = Depends(get_db) ):
    # Verify the token is not blacklisted
    await verify_token_not_blacklisted(request, token)
    current_username = await get_current_user(token)
    # Fetch user from database based on username
    result = await db.execute(select(User).filter(User.username == current_username))
    user = result.scalar_one_or_none()

    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    if not user or not pwd_context.verify(update_request.current_password, user.password):
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Current password is incorrect")

    if update_request.new_email:
        user.email = update_request.new_email

    if update_request.new_password:
        user.password = pwd_context.hash(update_request.new_password)

    await db.commit()
    return {"detail": "User updated successfully"}



@app.post("/get_user_preference/{preference}")
async def get_user_preference(request: Request, preference: str, token: str = Depends(oauth2_scheme), db: AsyncSession = Depends(get_db) ):
    # Verify the token is not blacklisted
    await verify_token_not_blacklisted(request, token)
    current_username = await get_current_user(token)
    # Ensure the preference is a valid column in the User model
    if not hasattr(User, preference):
        raise HTTPException(status_code=400, detail="Invalid preference name")

    # Dynamically select the column based on the preference name
    preference_column = getattr(User, preference)

    # Fetch the preference value for the current user
    result = await db.execute(select(preference_column).filter(User.username == current_username))
    preference_value = result.scalar_one_or_none()

    if preference_value is None:
        raise HTTPException(status_code=404, detail="Preference not found")

    return {preference: preference_value}





@app.post("/retrieve-data-per-user")
async def retrieve_data_per_user(request: Request, token: str = Depends(oauth2_scheme), db: AsyncSession = Depends(get_db) ) -> List[Dict]:
    # Verify the token is not blacklisted
    await verify_token_not_blacklisted(request, token)
    # Get the current user's username from the token
    current_username = await get_current_user(token)

    # Get the user's ID from the username
    user_id = await get_user_id_by_username(current_username, db)

    # Query the database for observations by user_id
    result = await db.execute(select(UserObservation).where(UserObservation.user_id == user_id))
    user_observations = result.scalars().all()

    # Convert the result to a list of dictionaries with specified fields
    observations_list = [
        {
            'feature1': observation.feature1,
            'feature2': observation.feature2,
            'feature3': observation.feature3,
            'feature4': observation.feature4,
            'date': observation.date
        }
        for observation in user_observations
    ]

    # Sort the list by date before returning
    sorted_observations_list = sorted(observations_list, key=lambda x: x['date'])

    return sorted_observations_list


@app.post("/delete_user_files")
async def delete_user_files(request: Request, token: str = Depends(oauth2_scheme), db: AsyncSession = Depends(get_db)):
    # Verify the token is not blacklisted
    await verify_token_not_blacklisted(request, token)

    # Get the current user's username from the token
    current_username = await get_current_user(token)

    # Get the user's ID from the username
    user_id = await get_user_id_by_username(current_username, db)

    # Delete entries from Observations table based on user_id
    try:
        await db.execute(UserObservation.__table__.delete().where(UserObservation.user_id == user_id))
        await db.commit()
        return {"message": "User files deleted successfully"}
    except Exception as e:
        return {"error": f"Failed to delete user files: {str(e)}"}




@app.post("/upload-encrypted-data", tags=["Encryption Methods"])
async def upload_encrypted_data(
    data: EncryptedData,
    request: Request,
    token: str = Depends(oauth2_scheme),
    db: AsyncSession = Depends(get_db)
):
    await verify_token_not_blacklisted(request, token)
    current_user_username = await get_current_user(token)
    current_user_id = await get_user_id_by_username(current_user_username, db)


    # Create a new record with the encrypted data
    new_record = UserObservationsEncrypted(
        user_id=current_user_id,
        encrypted_data=data.encryptedData,
        iv=data.iv
    )

    # Add to the database session
    db.add(new_record)

    # Commit the transaction
    await db.commit()

    return {"message": "Encrypted Data uploaded successfully"}



@app.get("/download-encrypted-data", tags=["Encryption Methods"])
async def download_encrypted_data(request: Request, token: str = Depends(oauth2_scheme), db: AsyncSession = Depends(get_db)):
    await verify_token_not_blacklisted(request, token)
    current_user_username = await get_current_user(token)
    current_user_id = await get_user_id_by_username(current_user_username, db)

    # Query encrypted data for the current user
    query = await db.execute(
        select(UserObservationsEncrypted.iv, UserObservationsEncrypted.encrypted_data)
        .filter(UserObservationsEncrypted.user_id == current_user_id)
    )

    # Extract iv and encrypted_data from query result
    results = query.all()
    encrypted_data_list = [
        {
            "iv": result.iv,
            "encryptedData": result.encrypted_data
        }
        for result in results if result
    ]

    return encrypted_data_list


@app.post("/verify-password-retrieve-salt")
async def verify_password_retrieve_salt(password: PasswordRequest, request: Request, token: str = Depends(oauth2_scheme), db: AsyncSession = Depends(get_db) ):
    # Verify the token is not blacklisted
    await verify_token_not_blacklisted(request, token)
    current_user = await get_current_user(token)

    result = await db.execute(select(User).filter(User.username == current_user))
    user = result.scalars().first()

    if user is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")

    # Verify password
    if not verify_password(password.password, user.password):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Incorrect password")

    return {"salt": user.salt}



@app.post("/download-onnx-optimizer")
async def download_onnx_optimizer(request: Request, token: str = Depends(oauth2_scheme)):
    # Verify the token is not blacklisted
    await verify_token_not_blacklisted(request, token)

    file_path = "ML/train_mlp_optimizer_model.onnx"
    if os.path.exists(file_path):
        return FileResponse(path=file_path, filename="train_mlp_optimizer_model.onnx", media_type='application/octet-stream')
    else:
        return {"error": "File not found"}

@app.post("/download-onnx-training-model")
async def download_onnx_training_model(request: Request, token: str = Depends(oauth2_scheme)):
    # Verify the token is not blacklisted
    await verify_token_not_blacklisted(request, token)

    file_path = "ML/train_mlp_training_model.onnx"
    if os.path.exists(file_path):
        return FileResponse(path=file_path, filename="train_mlp_training_model.onnx", media_type='application/octet-stream')
    else:
        return {"error": "File not found"}


@app.post("/download-onnx-training-eval-model")
async def download_onnx_training_eval_model(request: Request, token: str = Depends(oauth2_scheme)):
    # Verify the token is not blacklisted
    await verify_token_not_blacklisted(request, token)

    file_path = "ML/train_mlp_eval_model.onnx"
    if os.path.exists(file_path):
        return FileResponse(path=file_path, filename="train_mlp_eval_model.onnx", media_type='application/octet-stream')
    else:
        return {"error": "File not found"}



@app.post("/download-FL-checkpoint")
async def download_fl_checkpoint(request: Request, token: str = Depends(oauth2_scheme)):
    # Verify the token is not blacklisted
    await verify_token_not_blacklisted(request, token)

    file_path = "FL/train_mlp_checkpoint"
    if os.path.exists(file_path):
        return FileResponse(path=file_path, filename="FL/train_mlp_checkpoint", media_type='application/octet-stream')
    else:
        return {"error": "File not found"}


@app.post("/upload-json-floats")
async def upload_json_floats(request: Request, file: UploadFile = File(...), token: str = Depends(oauth2_scheme)):
    await verify_token_not_blacklisted(request, token)
    try:
        # Read the uploaded file's content
        contents = await file.read()

        # Save the file content to a local file
        current_username = await get_current_user(token)
        current_round = await redis_client.get('global_FL_round')
        filename = f"round_{current_round}_{current_username}.json"
        round_folder = Path(f"FL/round_{current_round}")
        round_folder.mkdir(parents=True, exist_ok=True)


        file_location = round_folder / filename

        # Save the file content to a local file
        async with aiofiles.open(file_location, 'wb') as f:
            await f.write(contents)


        return {"Message": "File saved and floats processed successfully"}

    except json.JSONDecodeError as e:
        raise HTTPException(status_code=400, detail=f"Invalid JSON format: {e}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"An error occurred: {e}")


@app.post("/generate_checkpoint")
async def generate_checkpoint():
    create_global_checkpoint()
    current_round = get_latest_round()
    current_round = current_round + 1
    await redis_client.set('global_FL_round', str(current_round))
    return {"status": "triggered aggregator"}



def get_latest_round():
    directory = './FL/'
    # List all folder names in the directory
    folder_names = [name for name in os.listdir(directory)
                    if os.path.isdir(os.path.join(directory, name)) and name != "tmp"]

    # Extract integers from the folder names
    round_numbers = [int(re.search(r'\d+', folder).group()) for folder in folder_names if re.search(r'\d+', folder)]

    # Find the highest integer
    highest_round = max(round_numbers) if round_numbers else 1

    return highest_round


# //////////////////////////////////////////
# Everything below this is under construction
# /////////////////////////////////////////

# Function to check if user is allowed
async def is_user_federated_learning(username: str, db: AsyncSession = Depends(get_db)):
    user =  await db.query(User).filter(User.username == username).first()
    if user and user.federated_learning:
        return True
    return False



# Dictionary to store WebSocket connections
websockets = {}

@app.websocket("/ws")
async def websocket_endpoint(websocket: WebSocket):
    await websocket.accept()
    token = websocket.query_params.get("token")

    if not token:
        await websocket.close(code=1008)  # Policy Violation
        return

    try:
        user = await get_current_user(token)
    except HTTPException:
        await websocket.close(code=1008)  # Policy Violation
        return

    client_id = f"user_{user}"
    websockets[client_id] = websocket
    await redis_client.sadd("connected_users", client_id)

    try:
        while True:
            # Receive data from WebSocket (if necessary)
            data = await websocket.receive_text()

    except WebSocketDisconnect:
        print(f"WebSocket disconnected for client_id {client_id}")
    finally:
        del websockets[client_id]
        await redis_client.srem("connected_users", client_id)
        await websocket.close()

@app.get("/connected_users")
async def get_connected_users():
    connected_users = await redis_client.smembers("connected_users")
    return {"connected_users": list(connected_users)}

async def broadcast_message(message: str):
    await redis_client.publish("websocket_channel", message)

@app.post("/broadcast")
async def broadcast_message_endpoint():
    message = "trigger_learning_round_" + str(await redis_client.get('global_FL_round'))
    await broadcast_message(message)
    return {"status": "trigger learning"}

async def determine_clients_to_send(connected_users):
    # Determine the number of clients to send the message to
    num_clients_to_send = max(1, int(len(connected_users) * 0.5))

    # Randomly select 50% of the clients
    clients_to_send = random.sample(connected_users, num_clients_to_send)

    return clients_to_send

async def process_message(channel, msg, redis_client, websockets):
    # Get the list of all connected WebSocket clients
    connected_users = await redis_client.smembers("connected_users")
    connected_users = list(connected_users)
    print("Connected users list:", connected_users)

    # Call the async function to determine the clients to send the message to
    clients_to_send = await determine_clients_to_send(connected_users)

    # Send message to the selected clients
    for client_id in clients_to_send:
        try:
            websocket = websockets[client_id]
            await websocket.send_text(msg)
        except Exception as e:
            print(f"Error sending message to a client: {e}")

async def listen_for_messages():
    pubsub = redis_client.pubsub()
    await pubsub.subscribe("websocket_channel")

    while True:
        message = await pubsub.get_message(ignore_subscribe_messages=True)
        if message:
            channel = message["channel"]
            msg = message["data"]
            print(f"Received message on {channel}: {msg}")
            # Call the async function to process the message
            await process_message(channel, msg, redis_client, websockets)