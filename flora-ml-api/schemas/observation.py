from pydantic import BaseModel
from datetime import datetime


class ObservationEntry(BaseModel):
    user_id: int
    observation_id: int
    feature1 : float
    feature2: float
    feature3: float
    feature4: float
    date: datetime

