# logger.py
from loguru import logger
from pathlib import Path

# Create logs directory if it doesn't exist
Path("logs").mkdir(parents=True, exist_ok=True)

# Configure Loguru logger
logger.add(
    "logs/app_{time:YYYY-MM-DD}.log",  # Log file path with rotation based on date
    rotation="500 MB",  # Rotate log files every 500 MB
    retention="30 days",  # Keep logs for 30 days
    level="DEBUG",  # Minimum level for log messages
    encoding="utf-8",  # Log file encoding
    enqueue=True,  # Use enqueue for better performance in high-throughput scenarios
)

"""
# Logger Options
# logger.debug("This is a debug message")
# logger.info("This is an info message")
# logger.warning("This is a warning message")
# logger.error("This is an error message")
# logger.critical("This is a critical message")
"""
