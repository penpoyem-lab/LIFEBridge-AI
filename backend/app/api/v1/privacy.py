import json
from datetime import datetime, timezone
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.db.session import get_db
from app.api.dependencies import get_current_user
from app.models.models import User
from app.schemas.schemas import (
    DataExportResponse, UserOut, DocumentOut, ActionCardItemSchema, SafetyCheckResponse
)
from app.repositories.user_repo import UserRepository

router = APIRouter(prefix="/privacy", tags=["Privacy & Data Rights"])

@router.get("/data-export", response_model=DataExportResponse)
@router.post("/data-export", response_model=DataExportResponse)
def export_user_data(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    docs = [
        DocumentOut(
            id=d.id,
            title=d.title,
            category=d.category,
            summary=d.summary,
            detected_language=d.detected_language,
            file_size_formatted=d.file_size_formatted,
            created_at=d.created_at
        )
        for d in current_user.documents
    ]

    cards = [
        ActionCardItemSchema(
            id=c.id,
            title=c.title,
            description=c.description,
            priority=c.priority,
            due_date=c.due_date,
            checklist=json.loads(c.checklist_json) if c.checklist_json else [],
            safe_next_step=c.safe_next_step,
            is_completed=c.is_completed,
            reminder_scheduled=c.reminder_scheduled
        )
        for c in current_user.action_cards
    ]

    checks = [
        SafetyCheckResponse(
            check_type=s.check_type,
            input_snippet=s.input_snippet,
            risk_level=s.risk_level,
            score=s.risk_score,
            indicators=json.loads(s.indicators_json) if s.indicators_json else [],
            safe_steps=json.loads(s.safe_steps_json) if s.safe_steps_json else [],
            explanation=s.explanation
        )
        for s in current_user.safety_checks
    ]

    return DataExportResponse(
        export_timestamp=datetime.now(timezone.utc),
        user=UserOut.from_orm(current_user),
        documents=docs,
        action_cards=cards,
        safety_checks=checks
    )

@router.post("/account-deletion")
def delete_account(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    user_email = current_user.email
    UserRepository.delete_user_and_all_data(db, user=current_user)
    return {
        "status": "success",
        "message": f"Account {user_email} and all associated documents, cards, and data have been permanently deleted."
    }
