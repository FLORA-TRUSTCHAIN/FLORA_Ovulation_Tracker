import uuid
from locust import HttpUser, task, between, SequentialTaskSet
import os

class UserBehavior(SequentialTaskSet):
    def on_start(self):
        self.unique_id = str(uuid.uuid4())[:8]
        self.username = f"user_{self.unique_id}"
        self.password = "password"
        self.email = f"{self.username}@example.com"
        self.token = None
        self.headers = {}
        self.register()
        self.login()

    def register(self):
        response = self.client.post(
            "/register",
            json={"username": self.username, "password": self.password, "email": self.email},
            headers={"Content-Type": "application/json"}
        )
        if response.status_code != 201:
            print(f"Failed to register: {response.text}")

    def login(self):
        response = self.client.post(
            "/login",
            data={"username": self.username, "password": self.password},
            headers={"Content-Type": "application/x-www-form-urlencoded"}
        )
        if response.status_code != 200:
            print(f"Failed to log in: {response.text}")
            return

        self.token = response.json().get("access_token")
        self.headers = {
            "Authorization": f"Bearer {self.token}",
            "Content-Type": "application/json"
        }

    @task
    def make_prediction(self):
        # Payload according to the PredictionRequest model
        prediction_request = {
            "features": [-2.76, -2.71, -1.09, 0.25]
        }

        model_name = "MLP"  # Replace with the actual model name

        response = self.client.post(
            f"/make_prediction/{model_name}",
            json=prediction_request,
            headers=self.headers
        )
        if response.status_code != 200:
            print(f"Failed to make prediction: {response.text}")

    def on_stop(self):
        if self.token:
            logout_headers = {
                "Authorization": f"Bearer {self.token}",
                "Content-Type": "application/json"
            }
            self.client.post("/logout", headers=logout_headers)


class FastAPIUser(HttpUser):
    tasks = [UserBehavior]
    wait_time = between(3, 9)
    host = "http://localhost:8000"
