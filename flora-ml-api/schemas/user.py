# schemas/user.py

from pydantic import BaseModel
from typing import Optional

class UserCreate(BaseModel):
    username: str
    password: str
    email: str


class UserDataResponse(BaseModel):
    username: str
    email: str
    access_type: str
    federated_learning: bool
    sharing4good: bool


class UpdateUserRequest(BaseModel):
    current_password: str
    new_email: Optional[str] = None
    new_password: Optional[str] = None



