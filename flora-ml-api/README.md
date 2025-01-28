# FLORA API

## Table of Contents
- [Description](#description)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Description

This FastAPI application serves as the back-end service for FLORA.

## Features
- OAuth2.0, JWT token authentication and CORS middleware
- Custom Logging
- Scheduled database jobs
- Federated Learning orchestration

## Installation

### Prerequisites
- Python 3.8+
- PostgreSQL v16 service running + Timescale Extension

#### Steps
1. Navigate to the project directory:
    ```sh
    cd yourproject
    ```
2. Create a virtual environment:
    ```sh
    python -m venv env
    ```
3. Activate the virtual environment:
    ```sh
    # On Windows
    .\env\Scripts\activate

    # On macOS/Linux
    source env/bin/activate
    ```
4. Install dependencies:
    ```sh
    pip install -r requirements.txt
    ```
## Usage

To start the FastAPI server, run:
``
uvicorn main:app --reload``
Navigate to `http://127.0.0.1:8000` in your browser to access the API. 
You can view the interactive API documentation at 
`http://127.0.0.1:8000/docs` or `http://127.0.0.1:8000/redoc`.


## API Documentation

-   Swagger UI: `http://127.0.0.1:8000/docs`
-   ReDoc: `http://127.0.0.1:8000/redoc`


## Configuration

Edit the `DATABASE_URL` string from `database/db_config.py` and insert your local database data.
Format: `"postgresql+asyncpg://<username>:<password>@<host>:<port>/<dbname>"`
Before running the application make sure to initialize the database tables.
This can be done by running the script `init.py` which can be found in `database/models/init.py`

## License 

(Coming Soon)
