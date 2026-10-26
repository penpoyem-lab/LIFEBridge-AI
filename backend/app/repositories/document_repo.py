import json
from sqlalchemy.orm import Session
from typing import List, Optional
from app.models.models import Document, DocumentAnalysis, ActionCard

class DocumentRepository:
    @staticmethod
    def get_all_by_user(db: Session, user_id: str, category: Optional[str] = None) -> List[Document]:
        query = db.query(Document).filter(Document.owner_id == user_id)
        if category and category != "All":
            query = query.filter(Document.category == category)
        return query.order_by(Document.created_at.desc()).all()

    @staticmethod
    def get_by_id(db: Session, doc_id: str, user_id: str) -> Optional[Document]:
        return db.query(Document).filter(Document.id == doc_id, Document.owner_id == user_id).first()

    @staticmethod
    def create_document_with_analysis(
        db: Session,
        user_id: str,
        title: str,
        category: str,
        content: str,
        summary: str,
        important_facts: List[str],
        dates: List[str],
        requirements: List[str],
        warnings: List[str],
        uncertainties: List[str],
        action_cards: List[dict]
    ) -> Document:
        file_size_formatted = f"{max(len(content) // 10, 48)} KB"
        doc = Document(
            owner_id=user_id,
            title=title,
            category=category,
            original_text=content,
            summary=summary,
            file_size_formatted=file_size_formatted
        )
        db.add(doc)
        db.flush()

        analysis = DocumentAnalysis(
            document_id=doc.id,
            owner_id=user_id,
            summary=summary,
            important_facts_json=json.dumps(important_facts),
            dates_json=json.dumps(dates),
            requirements_json=json.dumps(requirements),
            warnings_json=json.dumps(warnings),
            uncertainties_json=json.dumps(uncertainties),
            needs_human_review=len(uncertainties) > 0
        )
        db.add(analysis)

        for card_data in action_cards:
            card = ActionCard(
                owner_id=user_id,
                document_id=doc.id,
                title=card_data.get("title", "Review Document"),
                description=card_data.get("description", ""),
                priority=card_data.get("priority", "Medium"),
                due_date=card_data.get("due_date"),
                checklist_json=json.dumps(card_data.get("checklist", [])),
                safe_next_step=card_data.get("safe_next_step")
            )
            db.add(card)

        db.commit()
        db.refresh(doc)
        return doc

    @staticmethod
    def delete_by_id(db: Session, doc_id: str, user_id: str) -> bool:
        doc = db.query(Document).filter(Document.id == doc_id, Document.owner_id == user_id).first()
        if not doc:
            return False
        db.delete(doc)
        db.commit()
        return True
