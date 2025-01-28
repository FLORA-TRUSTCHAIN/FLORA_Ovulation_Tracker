from typing import List

from pydantic import BaseModel


class EncryptedData(BaseModel):
    iv: str
    encryptedData: str


class PasswordRequest(BaseModel):
    password: str

