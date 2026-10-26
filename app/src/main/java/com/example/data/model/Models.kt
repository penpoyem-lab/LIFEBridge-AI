package com.example.data.model

import org.json.JSONArray
import java.util.UUID

data class LanguageOption(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val isOfflineSupported: Boolean = true
)

val SupportedLanguages = listOf(
    LanguageOption("en", "English", "English", true),
    LanguageOption("bn", "Bengali", "বাংলা", true),
    LanguageOption("hi", "Hindi", "हिन्दी", true),
    LanguageOption("es", "Spanish", "Español", true),
    LanguageOption("fr", "French", "Français", true)
)

data class DocumentExplanation(
    val documentType: String,
    val detectedLanguage: String,
    val summary: String,
    val importantFacts: List<String>,
    val dates: List<String>,
    val requirements: List<String>,
    val warnings: List<String>,
    val actionCards: List<ActionCardItem>,
    val uncertainties: List<String>,
    val needsHumanReview: Boolean = false
)

data class ActionCardItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val priority: String = "Medium", // High, Medium, Low
    val dueDate: String? = null,
    val checklist: List<String> = emptyList(),
    val safeNextStep: String? = null
)

data class ScamAnalysis(
    val id: String = UUID.randomUUID().toString(),
    val checkType: String,
    val inputSnippet: String,
    val riskLevel: String, // "High Risk", "Warning Signs", "No Obvious Signs", "Unable to Verify"
    val score: Int, // 0 - 100
    val indicators: List<String>,
    val safeSteps: List<String>,
    val explanation: String
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timeFormatted: String,
    val category: String, // "Important", "System", "Reminder"
    val isRead: Boolean = false
)

fun parseJsonStringList(jsonStr: String): List<String> {
    return try {
        val list = mutableListOf<String>()
        val arr = JSONArray(jsonStr)
        for (i in 0 until arr.length()) {
            list.add(arr.getString(i))
        }
        list
    } catch (e: Exception) {
        if (jsonStr.isNotBlank() && !jsonStr.startsWith("[")) listOf(jsonStr) else emptyList()
    }
}

fun toJsonStringList(list: List<String>): String {
    val arr = JSONArray()
    list.forEach { arr.put(it) }
    return arr.toString()
}
