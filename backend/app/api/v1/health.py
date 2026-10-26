from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from sqlalchemy import text
from app.db.session import get_db
from app.core.config import settings

router = APIRouter(tags=["Health"])

@router.get("/health")
def health_check(db: Session = Depends(get_db)):
    db_status = "connected"
    try:
        db.execute(text("SELECT 1"))
    except Exception as e:
        db_status = f"unhealthy: {str(e)}"

    return {
        "status": "healthy" if db_status == "connected" else "degraded",
        "service": "LIFEBridge AI Backend API",
        "environment": settings.APP_ENV,
        "database": db_status,
        "ai_orchestration": {
            "provider": settings.AI_PROVIDER,
            "model": settings.AI_MODEL,
            "status": "active"
        }
    }
