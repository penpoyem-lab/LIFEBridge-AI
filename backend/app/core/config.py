from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import Optional

class Settings(BaseSettings):
    APP_NAME: str = "LIFEBridge AI API"
    APP_ENV: str = "development"
    LOG_LEVEL: str = "INFO"
    APP_SECRET_KEY: str = "dev_secret_key_change_in_production_32_chars"
    DATABASE_URL: str = "sqlite:///./lifebridge.db"
    REDIS_URL: Optional[str] = None
    AI_PROVIDER: str = "gemini"
    AI_API_KEY: Optional[str] = None
    AI_MODEL: str = "gemini-1.5-flash"
    MAX_UPLOAD_SIZE_MB: int = 20
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24

    model_config = SettingsConfigDict(
        env_file=".env",
        extra="ignore"
    )

settings = Settings()
