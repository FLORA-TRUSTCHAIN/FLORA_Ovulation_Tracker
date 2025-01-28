# models/user.py

from sqlalchemy import Column, Integer, String, Boolean, UniqueConstraint
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class User(Base):
    __tablename__ = 'users'

    id = Column(Integer, primary_key=True, index=True)
    username = Column(String(50), unique=True, nullable=False)
    password = Column(String, nullable=False)
    email = Column(String(100), unique=True, nullable=False)
    role = Column(String(20))
    access_type = Column(String(20))
    federated_learning = Column(Boolean, default=True, nullable=False)
    sharing4good = Column(Boolean, default=True, nullable=False)
    salt = Column(String(50), unique=True, nullable=False)

    # Optional: Add a unique constraint for (username, email) if needed
    UniqueConstraint('username', 'email', name='unique_username_email')
