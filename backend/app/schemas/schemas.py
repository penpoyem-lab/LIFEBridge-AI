from pydantic import BaseModel, EmailStr, Field
from typing import List, Optional
from datetime import datetime

# --- Auth Schemas ---
class UserRegister(BaseModel):
    email: EmailStr
    password: str = Field(..., min_length=6)
    display_name: str = Field(default="User", max_length=100)
    preferred_language: str = Field(default="en", max_length=10)

class UserLogin(BaseModel):
    email: EmailStr
    password: str

class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user_id: str
    display_name: str
    email: str

class PasswordResetRequest(BaseModel):
    email: EmailStr

class PasswordResetConfirm(BaseModel):
    email: EmailStr
    reset_token: str
    new_password: str = Field(..., min_length=6)

# --- User & Preferences Schemas ---
class UserOut(BaseModel):
    id: str
    email: str
    display_name: str
    preferred_language: str
    is_active: bool
    created_at: datetime

    class Config:
        from_attributes = True

class UserUpdate(BaseModel):
    display_name: Optional[str] = None
    preferred_language: Optional[str] = None

class UserPreferenceOut(BaseModel):
    theme: str
    voice_speed: float
    notifications_enabled: bool
    offline_mode: bool

    class Config:
        from_attributes = True

class UserPreferenceUpdate(BaseModel):
    theme: Optional[str] = None
    voice_speed: Optional[float] = None
    notifications_enabled: Optional[bool] = None
    offline_mode: Optional[bool] = None

# --- Action Card Schemas ---
class ActionCardItemSchema(BaseModel):
    id: str
    title: str
    description: str
    priority: str
    due_date: Optional[str] = None
    checklist: List[str] = []
    safe_next_step: Optional[str] = None
    is_completed: bool = False
    reminder_scheduled: bool = False

class ActionCardCreate(BaseModel):
    title: str
    description: str
    priority: str = "Medium"
    due_date: Optional[str] = None
    checklist: List[str] = []
    safe_next_step: Optional[str] = None
    document_id: Optional[str] = None

class ActionCardUpdate(BaseModel):
    title: Optional[str] = None
    description: Optional[str] = None
    priority: Optional[str] = None
    due_date: Optional[str] = None
    is_completed: Optional[bool] = None
    reminder_scheduled: Optional[bool] = None

# --- Document Schemas ---
class DocumentCreate(BaseModel):
    title: str
    content: str
    category: Optional[str] = None
    target_language: Optional[str] = "English"

class DocumentOut(BaseModel):
    id: str
    title: str
    category: str
    summary: Optional[str] = None
    detected_language: str
    file_size_formatted: str
    created_at: datetime

    class Config:
        from_attributes = True

class DocumentAnalysisResponse(BaseModel):
    document_id: str
    title: str
    category: str
    summary: str
    important_facts: List[str]
    dates: List[str]
    requirements: List[str]
    warnings: List[str]
    action_cards: List[ActionCardItemSchema]
    uncertainties: List[str]
    needs_human_review: bool

# --- Safety Schemas ---
class SafetyCheckRequest(BaseModel):
    content: str = Field(..., min_length=1)
    check_type: str = "Message" # "Message" or "Link"

class SafetyCheckResponse(BaseModel):
    check_type: str
    input_snippet: str
    risk_level: str
    score: int
    indicators: List[str]
    safe_steps: List[str]
    explanation: str

# --- Translation Schemas ---
class TranslationRequest(BaseModel):
    text: str = Field(..., min_length=1)
    target_language: str

class TranslationResponse(BaseModel):
    translated_text: str
    source_language: str
    target_language: str

class LiveVoiceRequest(BaseModel):
    text: str = Field(..., min_length=1)
    source_language: str = "en"
    target_language: str = "es"
    speaker_role: str = "you" # "you" or "partner"

class LiveVoiceResponse(BaseModel):
    original_text: str
    translated_text: str
    source_language: str
    target_language: str
    pronunciation_guide: Optional[str] = None
    speaker_role: str
    timestamp: datetime

class LanguageInfo(BaseModel):
    code: str
    display_name: str
    native_name: str
    flag_emoji: str
    supports_speech: bool = True

# --- Note Recall Schemas ---
class NoteRecallCreate(BaseModel):
    title: str
    snippet: str
    category: str = "Notice"
    document_id: Optional[str] = None
    scheduled_time: str = "Today, 6:00 PM"
    accent_color_hex: str = "#EBDBD3"
    audio_note_text: Optional[str] = None

class NoteRecallUpdate(BaseModel):
    title: Optional[str] = None
    snippet: Optional[str] = None
    category: Optional[str] = None
    scheduled_time: Optional[str] = None
    is_triggered: Optional[bool] = None
    is_done: Optional[bool] = None
    accent_color_hex: Optional[str] = None
    audio_note_text: Optional[str] = None

class NoteRecallOut(BaseModel):
    id: str
    title: str
    snippet: str
    category: str
    document_id: Optional[str] = None
    scheduled_time: str
    is_triggered: bool
    is_done: bool
    accent_color_hex: str
    audio_note_text: Optional[str] = None
    created_at: datetime

    class Config:
        from_attributes = True

# --- Reminder Schemas ---
class ReminderCreate(BaseModel):
    action_card_id: str
    scheduled_time: datetime

class ReminderOut(BaseModel):
    id: str
    action_card_id: str
    scheduled_time: datetime
    is_delivered: bool
    created_at: datetime

    class Config:
        from_attributes = True

# --- Privacy & Export Schemas ---
class DataExportResponse(BaseModel):
    application: str = "LIFEBridge AI"
    export_timestamp: datetime
    user: UserOut
    documents: List[DocumentOut]
    action_cards: List[ActionCardItemSchema]
    safety_checks: List[SafetyCheckResponse]
    note_recalls: List[NoteRecallOut] = []
