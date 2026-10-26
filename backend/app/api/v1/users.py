from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.db.session import get_db
from app.api.dependencies import get_current_user
from app.models.models import User
from app.schemas.schemas import UserOut, UserUpdate, UserPreferenceOut, UserPreferenceUpdate
from app.repositories.user_repo import UserRepository

router = APIRouter(prefix="/users", tags=["Users"])

@router.get("/me", response_model=UserOut)
def get_current_user_profile(user: User = Depends(get_current_user)):
    return user

@router.patch("/me", response_model=UserOut)
def update_current_user_profile(
    payload: UserUpdate,
    user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    updated = UserRepository.update_profile(
        db=db,
        user=user,
        display_name=payload.display_name,
        preferred_language=payload.preferred_language
    )
    return updated

@router.get("/me/preferences", response_model=UserPreferenceOut)
def get_user_preferences(
    user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    pref = user.preferences
    if not pref:
        pref = UserRepository.update_preferences(db, user_id=user.id)
    return pref

@router.patch("/me/preferences", response_model=UserPreferenceOut)
def update_user_preferences(
    payload: UserPreferenceUpdate,
    user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    pref = UserRepository.update_preferences(
        db,
        user_id=user.id,
        theme=payload.theme,
        voice_speed=payload.voice_speed,
        notifications_enabled=payload.notifications_enabled,
        offline_mode=payload.offline_mode
    )
    return pref
