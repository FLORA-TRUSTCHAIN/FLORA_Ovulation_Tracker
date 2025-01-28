from ML.models.LR.predict import *
from ML.models.MLP.inference import *
from schemas.prediction_request import PredictionRequest
from schemas.prediction_response import PredictionResponseLR, PredictionResponseMLP

def execute_model(model: str, prediction_request: PredictionRequest):
    if model == "MLP":
        result = Inference(model_name="mlp")
        labels, probabilities = result.predict(prediction_request.features)
        return PredictionResponseMLP(labels=labels, probabilities=probabilities)
    elif model == "LR":
        result = make_prediction(prediction_request.features)
        return PredictionResponseLR(labels=result)
