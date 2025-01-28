# models/user_observations.py

from sqlalchemy import Column, Integer, Float, TIMESTAMP
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class UserObservation(Base):
    __tablename__ = 'user_observations'

    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    user_id = Column(Integer, nullable=False)
    observation_id = Column(Integer, nullable=False)
    feature1 = Column(Float, nullable=False)
    feature2 = Column(Float, nullable=False)
    feature3 = Column(Float, nullable=False)
    feature4 = Column(Float, nullable=False)
    date = Column(TIMESTAMP(timezone=True), nullable=False)


