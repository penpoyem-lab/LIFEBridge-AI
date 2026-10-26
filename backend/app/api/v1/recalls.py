from datetime import datetime, timezone
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.db.session import get_db
from app.api.dependencies import get_optional_current_user
from app.models.models import User, NoteRecall
from app.schemas.schemas import NoteRecallCreate, NoteRecallOut, NoteRecallUpdate
from app.repositories.user_repo import UserRepository

router = APIRouter(prefix="/recalls", tags=["Note Recalls"])

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

def seed_default_recalls_if_empty(db: Session, user_id: str):
    count = db.query(NoteRecall).filter(NoteRecall.owner_id == user_id).count()
    if count == 0:
        samples = [
            NoteRecall(
                owner_id=user_id,
                title="⚡ Electricity Early Rebate Deadline",
                snippet="Save ₹350 on consumer ID #9482910 by paying before 5:00 PM today via official utility gateway.",
                category="Deadline",
                scheduled_time="Today, 4:00 PM",
                is_triggered=True,
                is_done=False,
                accent_color_hex="#EBD368", # PastelWheat
                audio_note_text="Reminder: Your electricity bill rebate of 350 rupees expires at 5 PM today. Consumer ID is 9482910."
            ),
            NoteRecall(
                owner_id=user_id,
                title="💊 Amoxicillin Dosage After Dinner",
                snippet="Take 500mg capsule with a full glass of water. Day 4 of 7-day prescribed antibiotic course.",
                category="Prescription",
                scheduled_time="Tonight, 8:30 PM",
                is_triggered=True,
                is_done=False,
                accent_color_hex="#EBDBD3", # Hudson
                audio_note_text="Recall notice: Take one 500 milligram Amoxicillin capsule with water after your evening meal."
            ),
            NoteRecall(
                owner_id=user_id,
                title="📑 Student Grant Income Certificate",
                snippet="Upload gazetted officer signed certificate for scholarship scheme grant up to ₹50,000.",
                category="Notice",
                scheduled_time="Tomorrow, 10:00 AM",
                is_triggered=False,
                is_done=False,
                accent_color_hex="#B9C7E0", # Penna
                audio_note_text="Important document recall: Government student scholarship requires uploading verified income certificate."
            )
        ]
        db.add_all(samples)
        db.commit()

@router.get("", response_model=List[NoteRecallOut])
def list_note_recalls(
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    user = current_user or get_or_create_default_user(db)
    seed_default_recalls_if_empty(db, user.id)
    recalls = db.query(NoteRecall).filter(NoteRecall.owner_id == user.id).order_by(NoteRecall.created_at.desc()).all()
    return recalls

@router.post("", response_model=NoteRecallOut, status_code=status.HTTP_201_CREATED)
def create_note_recall(
    payload: NoteRecallCreate,
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    user = current_user or get_or_create_default_user(db)
    recall = NoteRecall(
        owner_id=user.id,
        title=payload.title,
        snippet=payload.snippet,
        category=payload.category,
        document_id=payload.document_id,
        scheduled_time=payload.scheduled_time,
        accent_color_hex=payload.accent_color_hex,
        audio_note_text=payload.audio_note_text or payload.snippet,
        is_triggered=True,
        is_done=False
    )
    db.add(recall)
    db.commit()
    db.refresh(recall)
    return recall

@router.post("/{recall_id}/snooze", response_model=NoteRecallOut)
def snooze_recall(
    recall_id: str,
    snooze_hours: int = 1,
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    user = current_user or get_or_create_default_user(db)
    recall = db.query(NoteRecall).filter(NoteRecall.id == recall_id, NoteRecall.owner_id == user.id).first()
    if not recall:
        raise HTTPException(status_code=404, detail="Note recall not found")
    
    recall.scheduled_time = f"Snoozed +{snooze_hours}h"
    recall.is_triggered = False
    db.commit()
    db.refresh(recall)
    return recall

@router.post("/{recall_id}/done", response_model=NoteRecallOut)
def mark_recall_done(
    recall_id: str,
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    user = current_user or get_or_create_default_user(db)
    recall = db.query(NoteRecall).filter(NoteRecall.id == recall_id, NoteRecall.owner_id == user.id).first()
    if not recall:
        raise HTTPException(status_code=404, detail="Note recall not found")
    
    recall.is_done = True
    recall.is_triggered = False
    db.commit()
    db.refresh(recall)
    return recall

@router.delete("/{recall_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_recall(
    recall_id: str,
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    user = current_user or get_or_create_default_user(db)
    recall = db.query(NoteRecall).filter(NoteRecall.id == recall_id, NoteRecall.owner_id == user.id).first()
    if not recall:
        raise HTTPException(status_code=404, detail="Note recall not found")
    
    db.delete(recall)
    db.commit()
    return None
