package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.data.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class ChatbotRepository(private val context: Context) {

    private val apiService = GeminiNetworkClient.apiService

    private val systemPrompt = """
        You are LIFEBridge AI, an intelligent, empathetic, and actionable life assistant.
        You specialize in helping users navigate life's complexities:
        1. Understanding official documents (scholarships, government notices, hospital bills, contracts).
        2. Detecting scams, phishing links, and social engineering threats.
        3. Live multi-language translation and voice communication.
        4. Scheduling critical deadlines, note recalls, and emergency preparation (Offline Life Kit).
        
        Guidelines:
        - Provide direct, concise, and structured answers (use bullet points and bold headers).
        - If the user asks about checking a suspicious link or message, advise them and mention LIFEBridge's built-in Scam Checker.
        - If the user asks about translating or scanning documents, guide them and mention LIFEBridge Universal Scanner & Live Voice Translate.
        - Always be accurate, practical, and helpful. Do not output vague generic fluff.
    """.trimIndent()

    suspend fun getAssistantResponse(
        userMessage: String,
        history: List<ChatMessage>
    ): ChatMessage = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val hasValidKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (hasValidKey) {
            try {
                val contentsList = mutableListOf<GeminiContent>()

                // Add recent history for conversational continuity
                val recentHistory = history.takeLast(6)
                for (msg in recentHistory) {
                    val role = if (msg.sender == MessageSender.USER) "user" else "model"
                    contentsList.add(GeminiContent(role = role, parts = listOf(GeminiPart(msg.text))))
                }
                contentsList.add(GeminiContent(role = roleUser, parts = listOf(GeminiPart(userMessage))))

                val request = GeminiRequest(
                    contents = contentsList,
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(systemPrompt))),
                    generationConfig = GeminiGenerationConfig(temperature = 0.6f, maxOutputTokens = 800)
                )

                val response = try {
                    apiService.generateContent(apiKey = apiKey, request = request)
                } catch (e: Exception) {
                    // Try fallback model
                    apiService.generateContentFallback(apiKey = apiKey, request = request)
                }

                if (response.isSuccessful) {
                    val candidate = response.body()?.candidates?.firstOrNull()
                    val responseText = candidate?.content?.parts?.firstOrNull()?.text
                    if (!responseText.isNullOrBlank()) {
                        val (actionLabel, actionRoute) = determineActionRoute(userMessage, responseText)
                        return@withContext ChatMessage(
                            text = responseText.trim(),
                            sender = MessageSender.ASSISTANT,
                            actionLabel = actionLabel,
                            actionRoute = actionRoute
                        )
                    }
                } else {
                    Log.w("ChatbotRepository", "Gemini API error code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ChatbotRepository", "Error calling Gemini API: ${e.message}", e)
            }
        }

        // Comprehensive Real Contextual Fallback Engine
        return@withContext generateExpertLocalResponse(userMessage)
    }

    private val roleUser = "user"

    private fun determineActionRoute(userMessage: String, responseText: String): Pair<String?, String?> {
        val combined = (userMessage + " " + responseText).lowercase(Locale.ROOT)
        return when {
            combined.contains("scam") || combined.contains("phishing") || combined.contains("fraud") || combined.contains("suspicious") || combined.contains("safety") -> {
                Pair("Open Scam & Safety Checker", "safety_checker")
            }
            combined.contains("scan") || combined.contains("camera") || combined.contains("ocr") || combined.contains("photo") -> {
                Pair("Launch Document Scanner", "scanner")
            }
            combined.contains("translate") || combined.contains("voice") || combined.contains("conversation") || combined.contains("speech") -> {
                Pair("Start Live Voice Translate", "voice_translate")
            }
            combined.contains("bill") || combined.contains("notice") || combined.contains("contract") || combined.contains("understand") || combined.contains("scholarship") -> {
                Pair("Analyze in Universal Input", "universal_input")
            }
            combined.contains("library") || combined.contains("saved document") || combined.contains("file") -> {
                Pair("View Document Library", "library")
            }
            combined.contains("recall") || combined.contains("reminder") || combined.contains("deadline") -> {
                Pair("View Note Recalls", "reminders")
            }
            combined.contains("emergency") || combined.contains("offline") || combined.contains("first aid") -> {
                Pair("Open Offline Life Kit", "offline_kit")
            }
            else -> Pair(null, null)
        }
    }

    private fun generateExpertLocalResponse(query: String): ChatMessage {
        val q = query.lowercase(Locale.ROOT).trim()

        val (text, actionLabel, actionRoute) = when {
            q.contains("scam") || q.contains("fake") || q.contains("lottery") || q.contains("otp") || q.contains("bank account blocked") || q.contains("urgent money") -> {
                Triple(
                    """
                    **Fraud & Scam Analysis Alert:**
                    
                    ⚠️ **Key Warning Signs:**
                    • Legitimate banks and government agencies NEVER demand OTPs or passwords over SMS/calls.
                    • Urgency ("Immediate action required within 24h") is a classic social engineering tactic.
                    • Unverified shortlinks (bit.ly, tinyurl, weird domains) often lead to credential harvesting.
                    
                    **Recommended Action:**
                    Do not click any links or transfer funds. Use the built-in **Scam Checker** to verify this message or URL safely.
                    """.trimIndent(),
                    "Verify in Scam Checker",
                    "safety_checker"
                )
            }

            q.contains("bill") || q.contains("electricity") || q.contains("kwh") || q.contains("hospital") || q.contains("medical bill") -> {
                Triple(
                    """
                    **Medical & Utility Bill Breakdown:**
                    
                    1. **Check Billing Period & Meter Reading:** Verify your starting and ending kWh/units.
                    2. **Itemized Charges:** Hospitals and utilities frequently bundle facility fees, surcharges, or miscellaneous admin costs.
                    3. **Financial Assistance & Discounts:** Many utilities offer low-income, senior, or prompt-pay reductions (often 10%–25% upon request).
                    4. **Due Date & Action:** Ensure payments are scheduled before the deadline to avoid late penalties.
                    
                    You can paste or scan this bill directly into **Universal Understanding** for automated fee extraction!
                    """.trimIndent(),
                    "Scan & Analyze Bill",
                    "universal_input"
                )
            }

            q.contains("translate") || q.contains("spanish") || q.contains("bengali") || q.contains("hindi") || q.contains("foreign") || q.contains("language") -> {
                Triple(
                    """
                    **Live Dual-Speaker Voice Translation:**
                    
                    LIFEBridge provides real-time spoken translation designed for face-to-face conversations:
                    • **Split-Screen Layout:** Top screen faces your conversation partner, bottom screen faces you.
                    • **Audio Waveform Visualizer:** Shows real-time speech activity and mic pickup.
                    • **Instant Voice Playback:** Natural voice pronunciation so both parties can hear and understand seamlessly.
                    
                    Tap below to launch the translation studio!
                    """.trimIndent(),
                    "Launch Live Voice Translate",
                    "voice_translate"
                )
            }

            q.contains("scholarship") || q.contains("college") || q.contains("university") || q.contains("tuition") || q.contains("student") -> {
                Triple(
                    """
                    **Student Scholarship & Grant Guidance:**
                    
                    📋 **Core Checklist for Applications:**
                    • **Academic Transcripts:** Certified grade sheets from recent semesters.
                    • **Income Verification:** Tax return / income certificate of parent/guardian.
                    • **Statement of Purpose:** Clear description of academic goals and economic need.
                    • **Deadlines:** Applications submitted even 1 hour late are automatically disqualified.
                    
                    Upload your scholarship guidelines into LIFEBridge to automatically extract required documents and deadline action cards!
                    """.trimIndent(),
                    "Analyze Scholarship Doc",
                    "universal_input"
                )
            }

            q.contains("offline") || q.contains("emergency") || q.contains("contact") || q.contains("disaster") || q.contains("no internet") -> {
                Triple(
                    """
                    **Offline Life Kit & Emergency Preparedness:**
                    
                    🛡️ **Your Critical Offline Resources:**
                    • **Local Data Storage:** All verified documents and safety guides remain accessible without Wi-Fi or cellular service.
                    • **One-Tap Emergency Contacts:** Rapid SOS call triggers to trusted contacts and local helplines (112 / 911 / 100).
                    • **Essential Checklists:** First-aid steps, power outage survival guidelines, and document backup tokens.
                    """.trimIndent(),
                    "Open Offline Life Kit",
                    "offline_kit"
                )
            }

            q.contains("recall") || q.contains("remind") || q.contains("deadline") || q.contains("snooze") -> {
                Triple(
                    """
                    **Aesthetic Note Recalls & Reminders:**
                    
                    Stay on top of critical life events:
                    • **Intelligent Snoozing:** Defer reminders by 1 hour with a single swipe.
                    • **Audio Recalls:** Listen to spoken reminder summaries when your hands are busy.
                    • **Automatic Card Sync:** Action items extracted from your scanned bills and documents automatically appear in your reminder schedule.
                    """.trimIndent(),
                    "View Note Recalls",
                    "reminders"
                )
            }

            q.contains("help") || q.contains("what can you do") || q.contains("who are you") || q.contains("feature") -> {
                Triple(
                    """
                    **Welcome to LIFEBridge AI! Here is how I can assist you:**
                    
                    1. 📄 **Document Understanding:** Decode complex bills, contracts, insurance notices, and official government letters into simple, actionable steps.
                    2. 🛡️ **Scam & Fraud Defense:** Analyze suspicious texts, emails, and links before you click.
                    3. 🗣️ **Live Voice Translation:** Converse seamlessly across languages with split-screen dual mics and voice playback.
                    4. ⏰ **Aesthetic Note Recalls:** Ensure you never miss a payment, application deadline, or prescription refill.
                    5. 🆘 **Offline Life Kit:** Emergency contacts and critical documents preserved on-device without internet.
                    
                    What would you like to explore first?
                    """.trimIndent(),
                    "Explore Universal Scanner",
                    "universal_input"
                )
            }

            else -> {
                Triple(
                    """
                    **LIFEBridge Assistant Analysis:**
                    
                    Regarding your query about "$query":
                    
                    • **Clear Next Steps:** You can record, scan, or verify this information directly within LIFEBridge.
                    • **Actionable Protection:** If this involves financial transactions, legal paperwork, or unfamiliar requests, always verify the source through official channels before sharing sensitive details.
                    • **Secure Storage:** Store key summaries and dates in your local Document Library so you can reference them anytime, even offline.
                    
                    Feel free to ask follow-up questions or paste any document text you would like me to dissect!
                    """.trimIndent(),
                    "Analyze in Universal Scanner",
                    "universal_input"
                )
            }
        }

        return ChatMessage(
            text = text,
            sender = MessageSender.ASSISTANT,
            actionLabel = actionLabel,
            actionRoute = actionRoute
        )
    }
}
