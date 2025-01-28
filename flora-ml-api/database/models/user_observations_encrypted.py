from sqlalchemy import Column, Integer, Text
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class UserObservationsEncrypted(Base):
    __tablename__ = 'user_observations_encrypted'

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(Integer)
    encrypted_data = Column(Text)
    iv = Column(Text)


