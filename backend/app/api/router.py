from fastapi import APIRouter
from app.api.v1 import (
    auth, users, documents, action_cards, safety, translation, privacy, health, recalls
)

api_router = APIRouter(prefix="/api/v1")

# Mount all domain routers
api_router.include_router(health.router)
api_router.include_router(auth.router)
api_router.include_router(users.router)
api_router.include_router(documents.router)
api_router.include_router(action_cards.router)
api_router.include_router(safety.router)
api_router.include_router(translation.router)
api_router.include_router(recalls.router)
api_router.include_router(privacy.router)
