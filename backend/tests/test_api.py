import pytest
from app.services.ai_provider import AIOrchestrator
from app.services.risk_analysis import RiskAnalysisService

def test_document_analysis_classification():
    content = "Government Student Scholarship Scheme: Submit application with income certificate before 30 June 2026."
    result = AIOrchestrator.analyze_document_content("Student Grant", content)
    assert result["category"] == "School" or result["category"] == "Government"
    assert len(result["action_cards"]) > 0
    assert any("2026" in d for d in result["dates"])

def test_safety_check_high_risk_urgency():
    suspicious = "URGENT: You won a lottery of 500000 rupees! Click https://bit.ly/prize immediately or forfeit."
    result = RiskAnalysisService.analyze_message_or_link(suspicious, "Message")
    assert result["risk_level"] in ["High Risk", "Warning Signs"]
    assert result["score"] >= 50
    assert len(result["indicators"]) >= 2

def test_ssrf_protection_blocks_localhost_and_metadata():
    # Test localhost loopback
    is_safe, msg = RiskAnalysisService.validate_url_safe_and_not_ssrf("http://127.0.0.1:8000/admin")
    assert not is_safe
    assert "private/internal" in msg or "Prohibited" in msg

    # Test AWS/GCP metadata IP
    is_safe_meta, meta_msg = RiskAnalysisService.validate_url_safe_and_not_ssrf("http://169.254.169.254/latest/meta-data/")
    assert not is_safe_meta

    # Test Private class C
    is_safe_c, c_msg = RiskAnalysisService.validate_url_safe_and_not_ssrf("http://192.168.1.1/")
    assert not is_safe_c

def test_translation_bengali_and_hindi():
    result_bn = AIOrchestrator.translate("Hello, how are you?", "Bengali")
    assert result_bn == "হ্যালো, আপনি কেমন আছেন?"

    result_hi = AIOrchestrator.translate("Hello, how are you?", "Hindi")
    assert result_hi == "नमस्ते, आप कैसे हैं?"
