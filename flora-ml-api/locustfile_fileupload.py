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
        self.file_paths = [
            'test/1500_rows.csv',
            'test/1500_rows2.csv',
            'test/1500_rows3.csv',
            'test/1500_rows4.csv',
            'test/1500_rows5.csv'
        ]
        self.files_uploaded = set()
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
        self.headers = {"Authorization": f"Bearer {self.token}"}

    def on_stop(self):
        if self.token:
            logout_headers = {
                "Authorization": f"Bearer {self.token}",
                "Content-Type": "application/json"
            }
            self.client.post("/logout", headers=logout_headers)

    @task(1)
    def upload_files(self):
        for file_path in self.file_paths:
            if file_path not in self.files_uploaded:
                with open(file_path, 'rb') as f:
                    files = {
                        'files': (os.path.basename(file_path), f, 'text/csv')
                    }
                    upload_headers = {
                        "Authorization": f"Bearer {self.token}"
                    }
                    response = self.client.post("/upload", headers=upload_headers, files=files)
                    if response.status_code == 200:
                        print(f"File {file_path} uploaded successfully")
                        self.files_uploaded.add(file_path)
                    else:
                        print(f"Failed to upload file {file_path}: {response.text}")

class FastAPIUser(HttpUser):
    tasks = [UserBehavior]
    wait_time = between(3, 9)
    host = "http://localhost:8000"
