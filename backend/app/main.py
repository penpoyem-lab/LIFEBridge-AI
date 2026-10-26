from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.core.config import settings
from app.core.logging import logger
from app.core.errors import LifeBridgeException, lifebridge_exception_handler, global_exception_handler
from app.db.session import engine, Base
from app.api.router import api_router

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup: Initialize database schema
    logger.info(f"Initializing {settings.APP_NAME} in {settings.APP_ENV} mode...")
    try:
        Base.metadata.create_all(bind=engine)
        logger.info("Database tables initialized successfully.")
    except Exception as e:
        logger.error(f"Database initialization error: {e}")
    yield
    # Shutdown
    logger.info(f"Shutting down {settings.APP_NAME}.")

app = FastAPI(
    title=settings.APP_NAME,
    description="Multilingual, privacy-conscious AI life assistant backend for LIFEBridge AI.",
    version="1.0.0",
    lifespan=lifespan
)

# Exception handlers
app.add_exception_handler(LifeBridgeException, lifebridge_exception_handler)
app.add_exception_handler(Exception, global_exception_handler)

# CORS configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Mount API routes
app.include_router(api_router)

@app.get("/")
def root():
    return {
        "service": settings.APP_NAME,
        "tagline": "Understand More. Worry Less. Take the Next Step.",
        "docs": "/docs",
        "health": "/api/v1/health"
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)
