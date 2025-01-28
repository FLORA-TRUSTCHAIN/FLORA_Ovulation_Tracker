from sqlalchemy import Column, Integer, String, create_engine, MetaData
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class CurrentOnlineUsers(Base):
    __tablename__ = 'current_online_users'
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String, unique=True, nullable=False)
    ip_address = Column(String, nullable=False)