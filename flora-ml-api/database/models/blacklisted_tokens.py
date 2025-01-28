# models/blacklisted_tokens.py

from sqlalchemy import Column, Integer, String
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.types import TIMESTAMP

Base = declarative_base()

class BlacklistedTokens(Base):
    __tablename__ = 'blacklisted_tokens'

    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    token = Column(String, unique=True, nullable=False)
    blacklisted_at = Column(TIMESTAMP(timezone=True), nullable=False)
