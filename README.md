# LIFEBridge AI 🌉
> **Understand More. Worry Less. Take the Next Step.**

LIFEBridge AI is a multilingual, privacy-conscious, AI-powered life assistant that helps people understand complicated information and turn it into practical next steps.

---

## 🌟 Core Pillars

1. **Universal Understanding Engine**:
   - Parses official government notices, school letters, scholarship guidelines, utility bills, receipts, and employment documents.
   - Extracts simple summaries, critical facts, milestone dates, required tasks, and warnings.
   - Generates interactive, checkable **Action Cards**.

2. **Multilingual & Voice Accessibility**:
   - Native support for English, Bengali (বাংলা), Hindi (हिन्दी), Spanish, and French.
   - Integrated push-to-talk voice assistant with real-time waveform visualizer and speech read-aloud (TTS).

3. **Scam & Link Risk Protection**:
   - Detects psychological urgency, advance-fee prize fraud, credential harvesting, and suspicious obscured links.
   - Provides plain-language explanations and safe verification steps.

4. **100% Offline Life Kit**:
   - Stores saved document summaries, action cards, and emergency numbers locally in an encrypted Room SQLite database.
   - Direct-dial emergency directory for Police (100), Ambulance (102), Fire (101), Women Helpline (1091), and Disaster Relief (1070).

5. **Privacy by Design**:
   - Zero advertising trackers or third-party data selling.
   - Transparent data export (open JSON) and one-tap complete account deletion.

---

## 📱 Technology Stack

### Android Frontend
- **Language**: Kotlin 2.2
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Architecture**: MVVM with Coroutines & StateFlow
- **Local Persistence**: Room SQLite Database + Jetpack DataStore Preferences
- **Theme**: Liquid Glass aesthetics with Deep Midnight Navy palette (`#070C1A`) and Electric Cyan / Royal Violet accents
- **Audio**: Android TextToSpeech & Audio Visualizer

### Backend API (`/backend`)
- **Framework**: Python 3.12 + FastAPI
- **Data Validation**: Pydantic v2
- **ORM & DB**: SQLAlchemy 2.x + Alembic + PostgreSQL / SQLite
- **Deployment**: Docker & Docker Compose

---

## 🚀 Building & Running

### Android App
```bash
# Run unit & Robolectric tests
gradle :app:testDebugUnitTest

# Build debug APK
gradle assembleDebug
```

### Backend Server
```bash
cd backend
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```
Open interactive Swagger documentation at `http://localhost:8000/docs`.
