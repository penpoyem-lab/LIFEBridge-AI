import json
from fastapi import APIRouter, Depends, HTTPException, status, UploadFile, File, Form
from sqlalchemy.orm import Session
from typing import List, Optional
from app.db.session import get_db
from app.api.dependencies import get_current_user, get_optional_current_user
from app.models.models import User
from app.schemas.schemas import DocumentCreate, DocumentOut, DocumentAnalysisResponse, ActionCardItemSchema
from app.repositories.document_repo import DocumentRepository
from app.repositories.user_repo import UserRepository
from app.services.ai_provider import AIOrchestrator
from app.services.document_parser import DocumentParserService

router = APIRouter(prefix="/documents", tags=["Documents"])

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

@router.post("", response_model=DocumentAnalysisResponse)
def create_and_analyze_document(
    payload: DocumentCreate,
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    user = current_user or get_or_create_default_user(db)

    # 1. AI Analysis
    analysis_dict = AIOrchestrator.analyze_document_content(
        title=payload.title,
        content=payload.content,
        category=payload.category,
        target_language=payload.target_language or "English"
    )

    # 2. Persist in Database
    doc = DocumentRepository.create_document_with_analysis(
        db=db,
        user_id=user.id,
        title=analysis_dict["title"],
        category=analysis_dict["category"],
        content=payload.content,
        summary=analysis_dict["summary"],
        important_facts=analysis_dict["important_facts"],
        dates=analysis_dict["dates"],
        requirements=analysis_dict["requirements"],
        warnings=analysis_dict["warnings"],
        uncertainties=analysis_dict["uncertainties"],
        action_cards=analysis_dict["action_cards"]
    )

    # Convert created action cards to response schema
    card_schemas = []
    for c in doc.action_cards:
        checklist = json.loads(c.checklist_json) if c.checklist_json else []
        card_schemas.append(
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

    return DocumentAnalysisResponse(
        document_id=doc.id,
        title=doc.title,
        category=doc.category,
        summary=doc.summary or "",
        important_facts=analysis_dict["important_facts"],
        dates=analysis_dict["dates"],
        requirements=analysis_dict["requirements"],
        warnings=analysis_dict["warnings"],
        action_cards=card_schemas,
        uncertainties=analysis_dict["uncertainties"],
        needs_human_review=analysis_dict["needs_human_review"]
    )

@router.post("/upload", response_model=DocumentAnalysisResponse)
async def upload_document_file(
    file: UploadFile = File(...),
    title: Optional[str] = Form(None),
    category: Optional[str] = Form(None),
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    user = current_user or get_or_create_default_user(db)
    file_bytes = await file.read()
    extracted_text, detected_type = DocumentParserService.extract_text_from_bytes(
        file_bytes=file_bytes,
        filename=file.filename or "uploaded_document",
        content_type=file.content_type or "application/octet-stream"
    )

    final_title = title or file.filename or f"Uploaded {detected_type}"

    payload = DocumentCreate(
        title=final_title,
        content=extracted_text,
        category=category
    )
    return create_and_analyze_document(payload, current_user=user, db=db)

@router.get("", response_model=List[DocumentOut])
def list_documents(
    category: Optional[str] = None,
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    user = current_user or get_or_create_default_user(db)
    return DocumentRepository.get_all_by_user(db, user_id=user.id, category=category)

@router.get("/{document_id}", response_model=DocumentOut)
def get_document_details(
    document_id: str,
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    user = current_user or get_or_create_default_user(db)
    doc = DocumentRepository.get_by_id(db, doc_id=document_id, user_id=user.id)
    if not doc:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Document not found.")
    return doc

@router.delete("/{document_id}")
def delete_document(
    document_id: str,
    current_user: Optional[User] = Depends(get_optional_current_user),
    db: Session = Depends(get_db)
):
    user = current_user or get_or_create_default_user(db)
    deleted = DocumentRepository.delete_by_id(db, doc_id=document_id, user_id=user.id)
    if not deleted:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Document not found.")
    return {"message": "Document deleted successfully."}
