package com.example.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.ActionCardEntity
import com.example.data.local.entities.DocumentEntity
import com.example.data.local.entities.EmergencyContactEntity
import com.example.data.local.entities.NoteRecallEntity
import com.example.data.local.entities.SafetyCheckEntity
import com.example.data.local.entities.AuditLogEntity
import com.example.data.model.*
import com.example.data.repository.LifeBridgeRepository
import com.example.data.repository.ExecutiveStats
import com.example.data.repository.ComplianceReport
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

class LifeBridgeViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    val repository = LifeBridgeRepository(application.applicationContext)

    // Room Database Flows
    val documents: StateFlow<List<DocumentEntity>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val actionCards: StateFlow<List<ActionCardEntity>> = repository.allCards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val safetyChecks: StateFlow<List<SafetyCheckEntity>> = repository.allSafetyChecks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val emergencyContacts: StateFlow<List<EmergencyContactEntity>> = repository.emergencyContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeNoteRecalls: StateFlow<List<NoteRecallEntity>> = repository.activeNoteRecalls
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNoteRecalls: StateFlow<List<NoteRecallEntity>> = repository.allNoteRecalls
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val executiveStats: StateFlow<ExecutiveStats> = repository.getExecutiveStatsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExecutiveStats())

    // Preferences Flows
    val currentLanguage = repository.preferences.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val themeMode = repository.preferences.themeModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "light")

    val isOfflineMode = repository.preferences.isOfflineModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val notificationsEnabled = repository.preferences.notificationsEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val voiceSpeed = repository.preferences.voiceSpeedFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    val userDisplayName = repository.preferences.displayNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Alex Kumar")

    val userEmail = repository.preferences.emailFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "alex@example.com")

    val isOnboarded = repository.preferences.isOnboardedFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Navigation and UI State
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    val currentScreenContextSuggestion: StateFlow<ScreenContextSuggestion> = _currentScreen.map { screen ->
        when (screen) {
            "home" -> ScreenContextSuggestion(
                prompt = "Executive Status: Student grant application deadline is June 30.",
                actionLabel = "View Summary",
                targetRoute = "library"
            )
            "universal_input", "scanner" -> ScreenContextSuggestion(
                prompt = "OCR Intelligence: Align camera squarely with seals & watermarks for 99% accuracy.",
                actionLabel = "Open Camera",
                targetRoute = "scanner"
            )
            "safety", "safety_checker" -> ScreenContextSuggestion(
                prompt = "Zero-Trust Shield: Scanned 12 communications with 0 malicious payloads.",
                actionLabel = "Test Message",
                targetRoute = "safety_checker"
            )
            "library" -> ScreenContextSuggestion(
                prompt = "Compliance Vault: Generate signed SHA-256 PDF report for legal or academic records.",
                actionLabel = "Select Record",
                targetRoute = "library"
            )
            else -> ScreenContextSuggestion(
                prompt = "Studio Assistant: Speak naturally in any language for real-time phonetics.",
                actionLabel = "Start HUD",
                targetRoute = "voice_translate"
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ScreenContextSuggestion("LIFEBridge Executive Active", "Start", "home"))

    private val _selectedDocument = MutableStateFlow<DocumentEntity?>(null)
    val selectedDocument: StateFlow<DocumentEntity?> = _selectedDocument.asStateFlow()

    // Compliance Report State
    private val _activeComplianceReport = MutableStateFlow<ComplianceReport?>(null)
    val activeComplianceReport: StateFlow<ComplianceReport?> = _activeComplianceReport.asStateFlow()

    // Studio Voice HUD Waveform State
    private val _isStudioVoiceHudActive = MutableStateFlow(false)
    val isStudioVoiceHudActive: StateFlow<Boolean> = _isStudioVoiceHudActive.asStateFlow()

    // Biometric Security Vault State
    private val _isVaultUnlocked = MutableStateFlow(false)
    val isVaultUnlocked: StateFlow<Boolean> = _isVaultUnlocked.asStateFlow()

    // Processing Progress State
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _processingProgress = MutableStateFlow(0f)
    val processingProgress: StateFlow<Float> = _processingProgress.asStateFlow()

    private val _processingStage = MutableStateFlow("Extracting text...")
    val processingStage: StateFlow<String> = _processingStage.asStateFlow()

    private val _lastExplanation = MutableStateFlow<DocumentExplanation?>(null)
    val lastExplanation: StateFlow<DocumentExplanation?> = _lastExplanation.asStateFlow()

    // Safety Checker State
    private val _lastSafetyAnalysis = MutableStateFlow<ScamAnalysis?>(null)
    val lastSafetyAnalysis: StateFlow<ScamAnalysis?> = _lastSafetyAnalysis.asStateFlow()

    private val _isCheckingSafety = MutableStateFlow(false)
    val isCheckingSafety: StateFlow<Boolean> = _isCheckingSafety.asStateFlow()

    // Translation State
    private val _translationInput = MutableStateFlow("Hello, how are you?")
    val translationInput: StateFlow<String> = _translationInput.asStateFlow()

    private val _translationOutput = MutableStateFlow("হ্যালো, আপনি কেমন আছেন?")
    val translationOutput: StateFlow<String> = _translationOutput.asStateFlow()

    private val _targetLanguage = MutableStateFlow("Bengali")
    val targetLanguage: StateFlow<String> = _targetLanguage.asStateFlow()

    // Voice Assistant State
    private val _isVoiceListening = MutableStateFlow(false)
    val isVoiceListening: StateFlow<Boolean> = _isVoiceListening.asStateFlow()

    private val _voiceTranscript = MutableStateFlow("Can you explain my student scholarship deadline?")
    val voiceTranscript: StateFlow<String> = _voiceTranscript.asStateFlow()

    private val _voiceResponse = MutableStateFlow("Your student scholarship application submission deadline is June 30, 2026. You need your income certificate and academic mark sheet.")
    val voiceResponse: StateFlow<String> = _voiceResponse.asStateFlow()

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Action Cards Filter
    private val _cardFilter = MutableStateFlow("All") // "All", "Pending", "Completed"
    val cardFilter: StateFlow<String> = _cardFilter.asStateFlow()

    // Feedback messages
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    // Chatbot Repository & State
    private val chatbotRepository = com.example.data.repository.ChatbotRepository(application.applicationContext)

    private val _isChatbotOpen = MutableStateFlow(false)
    val isChatbotOpen: StateFlow<Boolean> = _isChatbotOpen.asStateFlow()

    private val _isChatbotThinking = MutableStateFlow(false)
    val isChatbotThinking: StateFlow<Boolean> = _isChatbotThinking.asStateFlow()

    private val _chatbotOffsetX = MutableStateFlow(0f)
    val chatbotOffsetX: StateFlow<Float> = _chatbotOffsetX.asStateFlow()

    private val _chatbotOffsetY = MutableStateFlow(0f)
    val chatbotOffsetY: StateFlow<Float> = _chatbotOffsetY.asStateFlow()

    val chatMessages: StateFlow<List<ChatMessage>> = repository.allChatMessages
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            listOf(
                ChatMessage(
                    text = "Hello! I am your LIFEBridge Assistant. I can help you decode complex documents, verify potential scams, translate conversations live, or organize your emergency kits. How can I help you today?",
                    sender = MessageSender.ASSISTANT
                )
            )
        )

    fun toggleChatbot(open: Boolean) {
        _isChatbotOpen.value = open
    }

    fun updateChatbotPosition(dx: Float, dy: Float) {
        _chatbotOffsetX.value = (_chatbotOffsetX.value + dx).coerceIn(-300f, 300f)
        _chatbotOffsetY.value = (_chatbotOffsetY.value + dy).coerceIn(-600f, 200f)
    }

    fun setChatbotPosition(x: Float, y: Float) {
        _chatbotOffsetX.value = x
        _chatbotOffsetY.value = y
    }

    fun clearChatMessages() {
        viewModelScope.launch {
            repository.clearChatHistory()
            val resetMsg = ChatMessage(
                text = "Chat history cleared. How can I help you now?",
                sender = MessageSender.ASSISTANT
            )
            repository.saveChatMessage(resetMsg)
        }
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(text = text.trim(), sender = MessageSender.USER)
        _isChatbotThinking.value = true

        viewModelScope.launch {
            repository.saveChatMessage(userMsg)
            try {
                val currentHistory = chatMessages.value
                val assistantMsg = chatbotRepository.getAssistantResponse(text, currentHistory)
                repository.saveChatMessage(assistantMsg)
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    text = "I encountered an issue processing your request. Please try again.",
                    sender = MessageSender.ASSISTANT,
                    isError = true
                )
                repository.saveChatMessage(errorMsg)
            } finally {
                _isChatbotThinking.value = false
            }
        }
    }

    // Text to Speech
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    init {
        textToSpeech = TextToSpeech(application.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.ENGLISH
            isTtsReady = true
        }
    }

    fun speak(text: String) {
        if (isTtsReady) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "lifebridge_tts")
        }
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
    }

    override fun onCleared() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onCleared()
    }

    // Navigation Methods
    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun selectDocument(doc: DocumentEntity) {
        _selectedDocument.value = doc
        _currentScreen.value = "document_detail"
    }

    fun setCardFilter(filter: String) {
        _cardFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setTranslationInput(text: String) {
        _translationInput.value = text
    }

    fun setTargetLanguage(lang: String) {
        _targetLanguage.value = lang
    }

    fun toggleOfflineMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.preferences.setOfflineMode(enabled)
            _snackbarMessage.emit(if (enabled) "Offline Mode Enabled" else "Online Mode Restored")
        }
    }

    fun toggleTheme(mode: String) {
        viewModelScope.launch {
            repository.preferences.setThemeMode(mode)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            repository.preferences.setLanguage(lang)
        }
    }

    fun setVoiceSpeed(speed: Float) {
        viewModelScope.launch {
            repository.preferences.setVoiceSpeed(speed)
            textToSpeech?.setSpeechRate(speed)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.preferences.setNotificationsEnabled(enabled)
        }
    }

    fun updateUserProfile(name: String, email: String) {
        viewModelScope.launch {
            repository.preferences.setUserProfile(name, email)
            _snackbarMessage.emit("Profile updated successfully")
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.preferences.setOnboarded(true)
            _currentScreen.value = "home"
        }
    }

    // Processing Pipeline Simulation with Real Storage
    fun processDocument(title: String, content: String, category: String? = null) {
        viewModelScope.launch {
            _isProcessing.value = true
            _processingProgress.value = 0.1f
            _processingStage.value = "Extracting text..."
            _currentScreen.value = "processing_progress"

            delay(600)
            _processingProgress.value = 0.35f
            _processingStage.value = "Detecting language & format..."

            delay(600)
            _processingProgress.value = 0.65f
            _processingStage.value = "Analyzing document structure & facts..."

            delay(700)
            _processingProgress.value = 0.90f
            _processingStage.value = "Generating action cards & verification steps..."

            delay(500)
            val explanation = repository.analyzeDocument(title, content, "English", category)
            _lastExplanation.value = explanation
            _processingProgress.value = 1.0f
            _isProcessing.value = false

            // Navigate to results
            _currentScreen.value = "explanation_results"
        }
    }

    // Safety Checker
    fun checkScamOrLink(input: String, type: String) {
        viewModelScope.launch {
            _isCheckingSafety.value = true
            delay(800) // Brief realistic analysis feel
            val result = repository.analyzeSafety(input, type)
            _lastSafetyAnalysis.value = result
            _isCheckingSafety.value = false
            _currentScreen.value = "safety_results"
        }
    }

    // Translation
    fun executeTranslation() {
        viewModelScope.launch {
            val result = repository.translateText(_translationInput.value, _targetLanguage.value)
            _translationOutput.value = result
        }
    }

    // Toggle Card Completion
    fun toggleCardCompleted(cardId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleCardCompleted(cardId, currentStatus)
            _snackbarMessage.emit(if (!currentStatus) "Task marked as completed! ✓" else "Task moved to pending")
        }
    }

    // Toggle Card Reminder
    fun toggleCardReminder(cardId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleCardReminder(cardId, currentStatus)
            _snackbarMessage.emit(if (!currentStatus) "Reminder scheduled for this deadline 🔔" else "Reminder cancelled")
        }
    }

    fun deleteCard(cardId: String) {
        viewModelScope.launch {
            repository.deleteCard(cardId)
            _snackbarMessage.emit("Action card deleted")
        }
    }

    fun toggleDocumentFavorite(docId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(docId)
        }
    }

    fun deleteDocument(docId: String) {
        viewModelScope.launch {
            repository.deleteDocument(docId)
            _snackbarMessage.emit("Document removed from library")
            _currentScreen.value = "library"
        }
    }

    // Voice simulation / push-to-talk
    fun toggleVoiceListening() {
        _isVoiceListening.value = !_isVoiceListening.value
        if (_isVoiceListening.value) {
            // Simulated voice listener recording
            viewModelScope.launch {
                delay(2000)
                _isVoiceListening.value = false
                _voiceTranscript.value = "What is the final deadline for the student support scheme?"
                _voiceResponse.value = "According to your saved Government Student Support notice, the final deadline is June 30, 2026. Make sure to upload your income certificate."
                speak(_voiceResponse.value)
            }
        }
    }

    // Note Recall System
    private val _dismissedRecallIds = MutableStateFlow<Set<String>>(emptySet())
    val dismissedRecallIds: StateFlow<Set<String>> = _dismissedRecallIds.asStateFlow()

    fun snoozeRecall(id: String) {
        viewModelScope.launch {
            repository.snoozeRecall(id, "Snoozed +1h")
            _snackbarMessage.emit("Note recall snoozed for 1 hour ⏰")
        }
    }

    fun completeRecall(id: String) {
        viewModelScope.launch {
            repository.markRecallDone(id)
            _snackbarMessage.emit("Note recall marked as completed! ✓")
        }
    }

    fun dismissRecallTemporarily(id: String) {
        _dismissedRecallIds.value = _dismissedRecallIds.value + id
    }

    fun readRecallAudio(text: String) {
        speak(text)
    }

    // Live Voice Translate (Google Translate Style)
    private val _liveConversation = MutableStateFlow<List<LiveConversationMessage>>(
        listOf(
            LiveConversationMessage(
                speaker = "You",
                originalText = "Hello, where is the nearest hospital?",
                translatedText = "¿Dónde está el hospital más cercano?",
                pronunciation = "DOHN-deh ehs-TAH ehl ohs-pee-TAHL",
                languageCode = "es"
            ),
            LiveConversationMessage(
                speaker = "Partner",
                originalText = "El hospital está a dos cuadras a la derecha.",
                translatedText = "The hospital is two blocks to the right.",
                pronunciation = null,
                languageCode = "en"
            )
        )
    )
    val liveConversation: StateFlow<List<LiveConversationMessage>> = _liveConversation.asStateFlow()

    private val _partnerLanguage = MutableStateFlow("Spanish")
    val partnerLanguage: StateFlow<String> = _partnerLanguage.asStateFlow()

    private val _isLiveTranslating = MutableStateFlow(false)
    val isLiveTranslating: StateFlow<Boolean> = _isLiveTranslating.asStateFlow()

    private val _activeMicRole = MutableStateFlow<String?>(null) // "you", "partner", or null
    val activeMicRole: StateFlow<String?> = _activeMicRole.asStateFlow()

    fun setPartnerLanguage(lang: String) {
        _partnerLanguage.value = lang
    }

    fun clearLiveConversation() {
        _liveConversation.value = emptyList()
    }

    fun sendLiveVoiceUtterance(text: String, isYou: Boolean) {
        viewModelScope.launch {
            _isLiveTranslating.value = true
            _activeMicRole.value = if (isYou) "you" else "partner"
            delay(500)

            val target = if (isYou) _partnerLanguage.value else "English"
            val source = if (isYou) "English" else _partnerLanguage.value
            val res = repository.liveVoiceTranslate(text, source, target, if (isYou) "you" else "partner")

            val newMsg = LiveConversationMessage(
                speaker = if (isYou) "You" else "Partner",
                originalText = text,
                translatedText = res.translatedText,
                pronunciation = res.pronunciation,
                languageCode = if (isYou) _partnerLanguage.value else "en"
            )
            _liveConversation.value = _liveConversation.value + newMsg
            _isLiveTranslating.value = false
            _activeMicRole.value = null

            // Read translated text aloud using TextToSpeech
            speak(res.translatedText)
        }
    }

    // Data Export
    fun exportData(onReady: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportUserData()
            onReady(json)
            _snackbarMessage.emit("Data exported successfully")
        }
    }

    // Account deletion
    fun deleteAccount() {
        viewModelScope.launch {
            repository.deleteAccountAndData()
            _snackbarMessage.emit("All account data deleted permanently")
            _currentScreen.value = "onboarding_1"
        }
    }

    // Executive Compliance Report Generation
    fun generateComplianceReportForSelected(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val doc = _selectedDocument.value ?: documents.value.firstOrNull()
            if (doc != null) {
                val report = repository.generateComplianceReport(doc)
                _activeComplianceReport.value = report
                _snackbarMessage.emit("Compliance Report #${report.reportId.takeLast(6)} Signed")
                onComplete()
            } else {
                _snackbarMessage.emit("Please select a document to generate report")
            }
        }
    }

    // Studio Voice Waveform HUD
    fun setStudioVoiceHudActive(active: Boolean) {
        _isStudioVoiceHudActive.value = active
    }

    // One-Tap Voice Memo Action
    fun saveVoiceMemo(transcript: String, translated: String? = null) {
        viewModelScope.launch {
            val card = repository.recordVoiceMemo(transcript, translated)
            _snackbarMessage.emit("Voice Memo logged: ${card.title}")
        }
    }

    // Biometric Vault Lock & Unlock
    fun unlockVault(pinOrBio: String = "1234"): Boolean {
        return if (pinOrBio.length >= 4) {
            _isVaultUnlocked.value = true
            viewModelScope.launch {
                repository.recordAuditLog("VAULT_BIOMETRIC_AUTH", "Biometric Hardware Gate Unlocked", "INFO")
                _snackbarMessage.emit("Security Vault Unlocked")
            }
            true
        } else {
            false
        }
    }

    fun lockVault() {
        _isVaultUnlocked.value = false
    }

    fun clearAuditLogs() {
        viewModelScope.launch {
            repository.clearAuditLogs()
            _snackbarMessage.emit("Audit trail cleared")
        }
    }
}

data class ScreenContextSuggestion(
    val prompt: String,
    val actionLabel: String,
    val targetRoute: String
)

data class LiveConversationMessage(
    val id: String = UUID.randomUUID().toString(),
    val speaker: String, // "You" or "Partner"
    val originalText: String,
    val translatedText: String,
    val pronunciation: String? = null,
    val languageCode: String,
    val timestamp: Long = System.currentTimeMillis()
)
