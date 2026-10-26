import json
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List, Optional
from app.db.session import get_db
from app.api.dependencies import get_current_user, get_optional_current_user
from app.models.models import User
from app.schemas.schemas import ActionCardItemSchema, ActionCardCreate, ActionCardUpdate
from app.repositories.action_card_repo import ActionCardRepository
from app.repositories.user_repo import UserRepository

router = APIRouter(prefix="/action-cards", tags=["Action Cards"])

def get_or_create_default_user(db: Session) -> User:
    user = UserRepository.get_by_email(db, "guest@lifebridge.local")
    if not user:
        user = UserRepository.create(
            db=db,
            email="guest@lifebridge.local",
            password="default_guest_password_secure",
            display_name="Guest User",
            preferred_language="en"
        )
    return user

@router.get("", response_model=List[ActionCardItemSchema])
def list_action_cards(
    status_filter: Optional[str] = None, # "Pending", "Completed", or None
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    user = current_user or get_or_create_default_user(db)
    cards = ActionCardRepository.get_all_by_user(db, user_id=user.id, status_filter=status_filter)
    result = []
    for c in cards:
        checklist = json.loads(c.checklist_json) if c.checklist_json else []
        result.append(
            ActionCardItemSchema(
                id=c.id,
                title=c.title,
                description=c.description,
                priority=c.priority,
                due_date=c.due_date,
                checklist=checklist,
                safe_next_step=c.safe_next_step,
                is_completed=c.is_completed,
                reminder_scheduled=c.reminder_scheduled
            )
        )
    return result

@router.post("", response_model=ActionCardItemSchema)
def create_action_card(
    payload: ActionCardCreate,
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    user = current_user or get_or_create_default_user(db)
    card = ActionCardRepository.create(
        db=db,
        user_id=user.id,
        title=payload.title,
        description=payload.description,
        priority=payload.priority,
        due_date=payload.due_date,
        checklist=payload.checklist,
        safe_next_step=payload.safe_next_step,
        document_id=payload.document_id
    )
    return ActionCardItemSchema(
        id=card.id,
        title=card.title,
        description=card.description,
        priority=card.priority,
        due_date=card.due_date,
        checklist=payload.checklist,
        safe_next_step=card.safe_next_step,
        is_completed=card.is_completed,
        reminder_scheduled=card.reminder_scheduled
    )

@router.patch("/{card_id}", response_model=ActionCardItemSchema)
def update_action_card(
    card_id: str,
    payload: ActionCardUpdate,
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    user = current_user or get_or_create_default_user(db)
    card = ActionCardRepository.get_by_id(db, card_id=card_id, user_id=user.id)
    if not card:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Action card not found.")

    card = ActionCardRepository.update(
        db,
        card=card,
        title=payload.title,
        description=payload.description,
        priority=payload.priority,
        due_date=payload.due_date,
        is_completed=payload.is_completed,
        reminder_scheduled=payload.reminder_scheduled
    )
    checklist = json.loads(card.checklist_json) if card.checklist_json else []
    return ActionCardItemSchema(
        id=card.id,
        title=card.title,
        description=card.description,
        priority=card.priority,
        due_date=card.due_date,
        checklist=checklist,
        safe_next_step=card.safe_next_step,
        is_completed=card.is_completed,
        reminder_scheduled=card.reminder_scheduled
    )

@router.post("/{card_id}/complete", response_model=ActionCardItemSchema)
def mark_card_completed(
    card_id: str,
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    user = current_user or get_or_create_default_user(db)
    card = ActionCardRepository.get_by_id(db, card_id=card_id, user_id=user.id)
    if not card:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Action card not found.")
    card = ActionCardRepository.update(db, card=card, is_completed=True)
    checklist = json.loads(card.checklist_json) if card.checklist_json else []
    return ActionCardItemSchema(
        id=card.id,
        title=card.title,
        description=card.description,
        priority=card.priority,
        due_date=card.due_date,
        checklist=checklist,
        safe_next_step=card.safe_next_step,
        is_completed=card.is_completed,
        reminder_scheduled=card.reminder_scheduled
    )

@router.delete("/{card_id}")
def delete_action_card(
    card_id: str,
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    user = current_user or get_or_create_default_user(db)
    card = ActionCardRepository.get_by_id(db, card_id=card_id, user_id=user.id)
    if not card:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Action card not found.")
    ActionCardRepository.delete(db, card=card)
    return {"message": "Action card deleted successfully."}
