# LIFEBridge AI Backend

FastAPI + SQLAlchemy 2.x + Pydantic + Docker production backend for LIFEBridge AI.

## Quickstart

### Local Python Execution
```bash
cd backend
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
uvicorn app.main:app --reload --port 8000
```

### Docker Compose
```bash
docker compose up --build
```

Interactive OpenAPI docs will be available at: `http://localhost:8000/docs`

## Features
- **Document Understanding Engine**: Text/OCR fact parsing, classification, dates extraction, and action card generation.
- **Scam & Link Risk Engine**: Urgency, phishing, and domain threat intelligence.
- **Multilingual Translation**: English, Bengali, Hindi, and more.
- **Data Export & Privacy by Design**: Zero-trust architecture, encrypted local caching.
