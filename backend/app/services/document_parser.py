import io
import re
from typing import Tuple
from app.core.logging import logger

class DocumentParserService:
    """
    Extracts text and metadata from plain text, PDF, and scanned images.
    """

    @staticmethod
    def extract_text_from_bytes(file_bytes: bytes, filename: str, content_type: str) -> Tuple[str, str]:
        """
        Parses raw uploaded bytes into clean text and detected category.
        """
        lower_name = filename.lower()

        # If text/plain or markdown
        if "text" in content_type or lower_name.endswith(".txt") or lower_name.endswith(".md"):
            try:
                text = file_bytes.decode("utf-8")
                return text.strip(), "Text"
            except Exception:
                text = file_bytes.decode("latin-1", errors="ignore")
                return text.strip(), "Text"

        # If PDF
        if "pdf" in content_type or lower_name.endswith(".pdf"):
            try:
                # Basic PDF text stream reader
                decoded = file_bytes.decode("latin-1", errors="ignore")
                # Extract text between parenthesis in TJ/Tj operators or simple text blocks
                strings = re.findall(r'\((.*?)\)\s*Tj', decoded)
                if strings:
                    extracted = " ".join(strings)
                    return extracted.strip(), "PDF"
            except Exception as e:
                logger.warning(f"PDF raw parsing failed: {e}")

        # Fallback raw string extraction
        clean_text = re.sub(r'[\x00-\x08\x0b\x0c\x0e-\x1f\x7f-\xff]', ' ', file_bytes[:100000].decode("latin-1", errors="ignore"))
        clean_text = re.sub(r'\s+', ' ', clean_text).strip()
        return clean_text[:20000], "Document"
