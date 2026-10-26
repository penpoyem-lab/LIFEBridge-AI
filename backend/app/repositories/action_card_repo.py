import json
from sqlalchemy.orm import Session
from typing import List, Optional
from app.models.models import ActionCard, SafetyCheckRecord

class ActionCardRepository:
    @staticmethod
    def get_all_by_user(db: Session, user_id: str, status_filter: Optional[str] = None) -> List[ActionCard]:
        query = db.query(ActionCard).filter(ActionCard.owner_id == user_id)
        if status_filter == "Pending":
            query = query.filter(ActionCard.is_completed == False)
        elif status_filter == "Completed":
            query = query.filter(ActionCard.is_completed == True)
        return query.order_by(ActionCard.created_at.desc()).all()

    @staticmethod
    def get_by_id(db: Session, card_id: str, user_id: str) -> Optional[ActionCard]:
        return db.query(ActionCard).filter(ActionCard.id == card_id, ActionCard.owner_id == user_id).first()

    @staticmethod
    def create(db: Session, user_id: str, title: str, description: str, priority: str, due_date: Optional[str], checklist: List[str], safe_next_step: Optional[str], document_id: Optional[str]) -> ActionCard:
        card = ActionCard(
            owner_id=user_id,
            document_id=document_id,
            title=title,
            description=description,
            priority=priority,
            due_date=due_date,
            checklist_json=json.dumps(checklist),
            safe_next_step=safe_next_step
        )
        db.add(card)
        db.commit()
        db.refresh(card)
        return card

    @staticmethod
    def update(db: Session, card: ActionCard, **kwargs) -> ActionCard:
        for k, v in kwargs.items():
            if v is not None and hasattr(card, k):
                setattr(card, k, v)
        db.commit()
        db.refresh(card)
        return card

    @staticmethod
    def delete(db: Session, card: ActionCard):
        db.delete(card)
        db.commit()

class SafetyRepository:
    @staticmethod
    def create_check(db: Session, user_id: Optional[str], check_type: str, input_snippet: str, risk_level: str, score: int, indicators: List[str], safe_steps: List[str], explanation: str) -> SafetyCheckRecord:
        record = SafetyCheckRecord(
            user_id=user_id,
            check_type=check_type,
            input_snippet=input_snippet[:255],
            risk_level=risk_level,
            risk_score=score,
            indicators_json=json.dumps(indicators),
            safe_steps_json=json.dumps(safe_steps),
            explanation=explanation
        )
        db.add(record)
        db.commit()
        db.refresh(record)
        return record

    @staticmethod
    def get_all_by_user(db: Session, user_id: str) -> List[SafetyCheckRecord]:
        return db.query(SafetyCheckRecord).filter(SafetyCheckRecord.user_id == user_id).order_by(SafetyCheckRecord.created_at.desc()).all()
