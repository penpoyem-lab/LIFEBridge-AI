import logging
import sys
from app.core.config import settings

def setup_logging():
    log_level_val = getattr(settings, "LOG_LEVEL", "INFO") or "INFO"
    log_level = getattr(logging, str(log_level_val).upper(), logging.INFO)
    logging.basicConfig(
        level=log_level,
        format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
        handlers=[logging.StreamHandler(sys.stdout)]
    )
    return logging.getLogger("lifebridge")

logger = setup_logging()
