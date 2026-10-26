import json
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from typing import List, Optional
from app.db.session import get_db
from app.api.dependencies import get_optional_current_user
from app.models.models import User
from app.schemas.schemas import SafetyCheckRequest, SafetyCheckResponse
from app.services.risk_analysis import RiskAnalysisService
from app.repositories.action_card_repo import SafetyRepository

router = APIRouter(prefix="/safety", tags=["Safety & Fraud Intelligence"])

@router.post("/message-check", response_model=SafetyCheckResponse)
@router.post("/check", response_model=SafetyCheckResponse)
def check_suspicious_message(
    payload: SafetyCheckRequest,
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    result = RiskAnalysisService.analyze_message_or_link(payload.content, check_type=payload.check_type)

    # Persist in DB
    user_id = current_user.id if current_user else None
    SafetyRepository.create_check(
        db=db,
        user_id=user_id,
        check_type=result["check_type"],
        input_snippet=result["input_snippet"],
        risk_level=result["risk_level"],
        score=result["score"],
        indicators=result["indicators"],
        safe_steps=result["safe_steps"],
        explanation=result["explanation"]
    )

    return SafetyCheckResponse(
        check_type=result["check_type"],
        input_snippet=result["input_snippet"],
        risk_level=result["risk_level"],
        score=result["score"],
        indicators=result["indicators"],
        safe_steps=result["safe_steps"],
        explanation=result["explanation"]
    )

@router.post("/url-check", response_model=SafetyCheckResponse)
def check_suspicious_url(
    payload: SafetyCheckRequest,
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    req = SafetyCheckRequest(content=payload.content, check_type="Link")
    return check_suspicious_message(req, current_user=current_user, db=db)

@router.get("/checks", response_model=List[SafetyCheckResponse])
def get_user_safety_checks(
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    if not current_user:
        return []
    records = SafetyRepository.get_all_by_user(db, user_id=current_user.id)
    return [
        SafetyCheckResponse(
            check_type=r.check_type,
            input_snippet=r.input_snippet,
            risk_level=r.risk_level,
            score=r.risk_score,
            indicators=json.loads(r.indicators_json) if r.indicators_json else [],
            safe_steps=json.loads(r.safe_steps_json) if r.safe_steps_json else [],
            explanation=r.explanation
        )
        for r in records
    ]
