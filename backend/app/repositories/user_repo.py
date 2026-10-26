from sqlalchemy.orm import Session
from typing import Optional, List
from app.models.models import User, UserPreference
from app.core.security import get_password_hash

class UserRepository:
    @staticmethod
    def get_by_email(db: Session, email: str) -> Optional[User]:
        return db.query(User).filter(User.email == email.lower()).first()

    @staticmethod
    def get_by_id(db: Session, user_id: str) -> Optional[User]:
        return db.query(User).filter(User.id == user_id).first()

    @staticmethod
    def create(db: Session, email: str, password: str, display_name: str, preferred_language: str) -> User:
        user = User(
            email=email.lower(),
            hashed_password=get_password_hash(password),
            display_name=display_name,
            preferred_language=preferred_language
        )
        db.add(user)
        db.flush()

        # Create default preferences
        pref = UserPreference(user_id=user.id)
        db.add(pref)
        db.commit()
        db.refresh(user)
        return user

    @staticmethod
    def update_profile(db: Session, user: User, display_name: Optional[str], preferred_language: Optional[str]) -> User:
        if display_name is not None:
            user.display_name = display_name
        if preferred_language is not None:
            user.preferred_language = preferred_language
        db.commit()
        db.refresh(user)
        return user

    @staticmethod
    def update_preferences(db: Session, user_id: str, **kwargs) -> UserPreference:
        pref = db.query(UserPreference).filter(UserPreference.user_id == user_id).first()
        if not pref:
            pref = UserPreference(user_id=user_id)
            db.add(pref)
        for k, v in kwargs.items():
            if v is not None and hasattr(pref, k):
                setattr(pref, k, v)
        db.commit()
        db.refresh(pref)
        return pref

    @staticmethod
    def delete_user_and_all_data(db: Session, user: User):
        db.delete(user)
        db.commit()
