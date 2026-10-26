from datetime import datetime, timezone
from typing import List
from fastapi import APIRouter
from app.schemas.schemas import (
    TranslationRequest, TranslationResponse,
    LiveVoiceRequest, LiveVoiceResponse, LanguageInfo
)
from app.services.ai_provider import AIOrchestrator

router = APIRouter(prefix="/translation", tags=["Translation"])

@router.post("", response_model=TranslationResponse)
def translate_text(payload: TranslationRequest):
    translated = AIOrchestrator.translate(payload.text, payload.target_language)
    return TranslationResponse(
        translated_text=translated,
        source_language="auto",
        target_language=payload.target_language
    )

@router.post("/live-voice", response_model=LiveVoiceResponse)
def live_voice_translation(payload: LiveVoiceRequest):
    result = AIOrchestrator.live_voice_translate(
        text=payload.text,
        source_lang=payload.source_language,
        target_lang=payload.target_language,
        speaker_role=payload.speaker_role
    )
    return LiveVoiceResponse(
        original_text=result["original_text"],
        translated_text=result["translated_text"],
        source_language=result["source_language"],
        target_language=result["target_language"],
        pronunciation_guide=result.get("pronunciation_guide"),
        speaker_role=result["speaker_role"],
        timestamp=datetime.now(timezone.utc)
    )

@router.get("/languages", response_model=List[LanguageInfo])
def get_languages():
    return [
        LanguageInfo(code="en", display_name="English", native_name="English", flag_emoji="🇺🇸", supports_speech=True),
        LanguageInfo(code="es", display_name="Spanish", native_name="Español", flag_emoji="🇪🇸", supports_speech=True),
        LanguageInfo(code="bn", display_name="Bengali", native_name="বাংলা", flag_emoji="🇧🇩", supports_speech=True),
        LanguageInfo(code="hi", display_name="Hindi", native_name="हिन्दी", flag_emoji="🇮🇳", supports_speech=True),
        LanguageInfo(code="fr", display_name="French", native_name="Français", flag_emoji="🇫🇷", supports_speech=True),
        LanguageInfo(code="de", display_name="German", native_name="Deutsch", flag_emoji="🇩🇪", supports_speech=True),
        LanguageInfo(code="ja", display_name="Japanese", native_name="日本語", flag_emoji="🇯🇵", supports_speech=True),
        LanguageInfo(code="ar", display_name="Arabic", native_name="العربية", flag_emoji="🇸🇦", supports_speech=True)
    ]
