import torch
import joblib
import numpy as np

from ML.models.MLP.models.mlp import MLP


class Inference:
    def __init__(self, model_name):
        if model_name in ['MLP', 'mlp']:
            self.model = MLP(input_dim=4)
            self.model_path = "ML/models/MLP/checkpoint/mlp/model.pth"
            self.scaler_path = "ML/models/MLP/checkpoint/mlp/scaler.pkl"
        else:
            raise ValueError(f'{model_name} cannot be found!')

        self.device = 'cuda' if torch.cuda.is_available() else 'cpu'
        self.model.load_state_dict(torch.load(self.model_path, map_location=self.device))
        self.model.to(self.device)
        self.model.eval()
        self.scaler = joblib.load(self.scaler_path)

    def preprocess(self, X):
        return self.scaler.transform(X)

    def predict(self, X):
        X = np.array(X)
        if len(X.shape) == 1:
            X = X.reshape(1, -1)

        X_scaled = self.preprocess(X)
        X_tensor = torch.tensor(X_scaled, dtype=torch.float32).to(self.device)
        with torch.no_grad():
            probabilities_class_1 = self.model(X_tensor).cpu().numpy().flatten()
        probabilities_class_0 = 1 - probabilities_class_1

        labels = (probabilities_class_1 > 0.5).astype(int)
        probabilities_class_0 = probabilities_class_0.tolist()
        probabilities_class_1 = probabilities_class_1.tolist()
        probabilities = [{'0': prob_0, '1': prob_1} for prob_0, prob_1 in
                         zip(probabilities_class_0, probabilities_class_1)]
        return list(labels), round_probabilities(probabilities)


def round_probabilities(probabilities):
    rounded_probabilities = [
        {key: round(value, 3) for key, value in prob.items()}
        for prob in probabilities
    ]
    return rounded_probabilities
