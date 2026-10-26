import time
from collections import defaultdict
from fastapi import HTTPException, status
from app.core.logging import logger

class SimpleRateLimiter:
    """In-memory rate limiter per client IP / user with sliding window."""
    def __init__(self, max_requests: int = 60, window_seconds: int = 60):
        self.max_requests = max_requests
        self.window_seconds = window_seconds
        self.requests = defaultdict(list)

    def check(self, key: str):
        now = time.time()
        timestamps = self.requests[key]
        # Purge older than window
        self.requests[key] = [t for t in timestamps if now - t < self.window_seconds]
        if len(self.requests[key]) >= self.max_requests:
            logger.warning(f"Rate limit exceeded for key: {key}")
            raise HTTPException(
                status_code=status.HTTP_429_TOO_MANY_REQUESTS,
                detail="Rate limit exceeded. Please wait a minute before retrying."
            )
        self.requests[key].append(now)

rate_limiter = SimpleRateLimiter(max_requests=100, window_seconds=60)
