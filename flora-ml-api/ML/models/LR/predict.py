import joblib
import numpy as np


def make_prediction(input_features):
    input_features = np.array(input_features)
    if len(input_features.shape) == 1:
        input_features = input_features.reshape(1, -1)

    # Load the saved model
    model = joblib.load("ML/models/LR/checkpoint/lr/logistic_regression_model.joblib")

    # Predict and return the result
    prediction = model.predict(input_features)
    return prediction

