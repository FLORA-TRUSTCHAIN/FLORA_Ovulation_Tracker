# Pydantic model for request validation

from pydantic import BaseModel, Field


class PredictionRequest(BaseModel):
    features: list = Field(..., example=[-2.76, -2.71, -1.09, 0.25])
