# database/db_config.py

from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from dotenv import load_dotenv
import os
# Load environment variables from .env file
load_dotenv()

DATABASE_URL = os.getenv("DB_URL")

# "postgresql+asyncpg://<username>:<password>@<host>:<port>/<dbname>"


# Configure the async engine
engine = create_async_engine(
    DATABASE_URL,
    echo=True,
    future=True,
    pool_size=20,           # Increase the pool size
    max_overflow=30,        # Allow extra connections beyond pool_size
    pool_timeout=30,        # Timeout to wait for a connection
    pool_recycle=1800,      # Recycle connections after 30 minutes
    pool_pre_ping=True      # Enable pool pre-ping to test connections before using them
)

# Configure the session factory
SessionLocal = sessionmaker(
    bind=engine,
    class_=AsyncSession,
    expire_on_commit=False
)

