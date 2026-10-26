import uuid
from datetime import datetime, timezone
from sqlalchemy import Column, String, Integer, Float, Boolean, Text, DateTime, ForeignKey, Index
from sqlalchemy.orm import relationship
from app.db.session import Base

def generate_uuid() -> str:
    return str(uuid.uuid4())

def get_utc_now() -> datetime:
    return datetime.now(timezone.utc)

class User(Base):
    __tablename__ = "users"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    email = Column(String(255), unique=True, index=True, nullable=False)
    hashed_password = Column(String(255), nullable=False)
    display_name = Column(String(100), default="User")
    preferred_language = Column(String(10), default="en")
    is_active = Column(Boolean, default=True)
    created_at = Column(DateTime, default=get_utc_now)
    updated_at = Column(DateTime, default=get_utc_now, onupdate=get_utc_now)

    # Relationships
    preferences = relationship("UserPreference", back_populates="user", uselist=False, cascade="all, delete-orphan")
    documents = relationship("Document", back_populates="owner", cascade="all, delete-orphan")
    action_cards = relationship("ActionCard", back_populates="owner", cascade="all, delete-orphan")
    reminders = relationship("Reminder", back_populates="owner", cascade="all, delete-orphan")
    safety_checks = relationship("SafetyCheckRecord", back_populates="user", cascade="all, delete-orphan")
    audit_logs = relationship("AuditLog", back_populates="user", cascade="all, delete-orphan")

class UserPreference(Base):
    __tablename__ = "user_preferences"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    user_id = Column(String(36), ForeignKey("users.id", ondelete="CASCADE"), unique=True, nullable=False)
    theme = Column(String(20), default="dark") # "dark" or "light"
    voice_speed = Column(Float, default=1.0)
    notifications_enabled = Column(Boolean, default=True)
    offline_mode = Column(Boolean, default=False)
    updated_at = Column(DateTime, default=get_utc_now, onupdate=get_utc_now)

    user = relationship("User", back_populates="preferences")

class Document(Base):
    __tablename__ = "documents"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    owner_id = Column(String(36), ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)
    title = Column(String(255), nullable=False, index=True)
    category = Column(String(50), default="General", index=True) # Government, Bills, School, Receipts, Employment, Other
    original_text = Column(Text, nullable=False)
    summary = Column(Text, nullable=True)
    detected_language = Column(String(50), default="English")
    file_size_formatted = Column(String(30), default="120 KB")
    storage_path = Column(String(512), nullable=True)
    created_at = Column(DateTime, default=get_utc_now, index=True)

    owner = relationship("User", back_populates="documents")
    analysis = relationship("DocumentAnalysis", back_populates="document", uselist=False, cascade="all, delete-orphan")
    action_cards = relationship("ActionCard", back_populates="document", cascade="all, delete-orphan")

class DocumentAnalysis(Base):
    __tablename__ = "document_analyses"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    document_id = Column(String(36), ForeignKey("documents.id", ondelete="CASCADE"), unique=True, nullable=False)
    owner_id = Column(String(36), ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    summary = Column(Text, nullable=False)
    important_facts_json = Column(Text, default="[]")
    dates_json = Column(Text, default="[]")
    requirements_json = Column(Text, default="[]")
    warnings_json = Column(Text, default="[]")
    uncertainties_json = Column(Text, default="[]")
    needs_human_review = Column(Boolean, default=False)
    ai_provider = Column(String(50), default="gemini")
    created_at = Column(DateTime, default=get_utc_now)

    document = relationship("Document", back_populates="analysis")

class ActionCard(Base):
    __tablename__ = "action_cards"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    owner_id = Column(String(36), ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)
    document_id = Column(String(36), ForeignKey("documents.id", ondelete="SET NULL"), nullable=True, index=True)
    title = Column(String(255), nullable=False)
    description = Column(Text, nullable=False)
    priority = Column(String(20), default="Medium") # High, Medium, Low
    due_date = Column(String(50), nullable=True)
    is_completed = Column(Boolean, default=False, index=True)
    checklist_json = Column(Text, default="[]")
    safe_next_step = Column(String(512), nullable=True)
    reminder_scheduled = Column(Boolean, default=False)
    created_at = Column(DateTime, default=get_utc_now, index=True)

    owner = relationship("User", back_populates="action_cards")
    document = relationship("Document", back_populates="action_cards")
    reminders = relationship("Reminder", back_populates="action_card", cascade="all, delete-orphan")

class Reminder(Base):
    __tablename__ = "reminders"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    owner_id = Column(String(36), ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)
    action_card_id = Column(String(36), ForeignKey("action_cards.id", ondelete="CASCADE"), nullable=False)
    scheduled_time = Column(DateTime, nullable=False)
    is_delivered = Column(Boolean, default=False)
    created_at = Column(DateTime, default=get_utc_now)

    owner = relationship("User", back_populates="reminders")
    action_card = relationship("ActionCard", back_populates="reminders")

class SafetyCheckRecord(Base):
    __tablename__ = "safety_checks"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    user_id = Column(String(36), ForeignKey("users.id", ondelete="CASCADE"), nullable=True, index=True)
    check_type = Column(String(20), nullable=False) # Message or Link
    input_snippet = Column(String(255), nullable=False)
    risk_level = Column(String(50), nullable=False) # High Risk, Warning Signs, No Obvious Signs
    risk_score = Column(Integer, default=0)
    indicators_json = Column(Text, default="[]")
    safe_steps_json = Column(Text, default="[]")
    explanation = Column(Text, nullable=False)
    created_at = Column(DateTime, default=get_utc_now, index=True)

    user = relationship("User", back_populates="safety_checks")

class AuditLog(Base):
    __tablename__ = "audit_logs"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    user_id = Column(String(36), ForeignKey("users.id", ondelete="CASCADE"), nullable=True)
    action = Column(String(100), nullable=False)
    ip_address = Column(String(45), nullable=True)
    created_at = Column(DateTime, default=get_utc_now)

    user = relationship("User", back_populates="audit_logs")

class NoteRecall(Base):
    __tablename__ = "note_recalls"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    owner_id = Column(String(36), ForeignKey("users.id", ondelete="CASCADE"), nullable=True, index=True)
    title = Column(String(255), nullable=False)
    snippet = Column(Text, nullable=False)
    category = Column(String(50), default="Notice") # Prescription, Deadline, Financial, Personal
    document_id = Column(String(36), ForeignKey("documents.id", ondelete="SET NULL"), nullable=True)
    scheduled_time = Column(String(100), default="Today, 6:00 PM")
    is_triggered = Column(Boolean, default=True)
    is_done = Column(Boolean, default=False)
    accent_color_hex = Column(String(20), default="#EBDBD3")
    audio_note_text = Column(Text, nullable=True)
    created_at = Column(DateTime, default=get_utc_now, index=True)
