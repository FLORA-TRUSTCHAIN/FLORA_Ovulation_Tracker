from pydantic import BaseModel

class PredictionResponseMLP(BaseModel):
    labels: list[int]
    probabilities: list[dict]

class PredictionResponseLR(BaseModel):
    labels: list[int]
