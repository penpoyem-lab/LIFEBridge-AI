package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val category: String, // Government, School, Bill, Receipt, Employment, Letter, Other
    val originalText: String,
    val summary: String,
    val detectedLanguage: String = "English",
    val importantFactsJson: String = "[]",
    val datesJson: String = "[]",
    val requirementsJson: String = "[]",
    val warningsJson: String = "[]",
    val uncertaintiesJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val fileSizeFormatted: String = "124 KB",
    val needsHumanReview: Boolean = false
)

@Entity(tableName = "action_cards")
data class ActionCardEntity(
    @PrimaryKey
    val id: String,
    val documentId: String? = null,
    val title: String,
    val description: String,
    val priority: String = "Medium", // High, Medium, Low
    val dueDate: String? = null,
    val isCompleted: Boolean = false,
    val checklistJson: String = "[]",
    val safeNextStep: String? = null,
    val reminderScheduled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "safety_checks")
data class SafetyCheckEntity(
    @PrimaryKey
    val id: String,
    val checkType: String, // "Message" or "Link"
    val inputContent: String,
    val riskLevel: String, // "High Risk", "Warning Signs", "No Obvious Signs", "Unable to Verify"
    val score: Int = 0, // 0 - 100 risk score
    val indicatorsJson: String = "[]",
    val safeStepsJson: String = "[]",
    val explanation: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "emergency_contacts")
data class EmergencyContactEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val number: String,
    val category: String, // Police, Medical, Fire, Helpline, Disaster
    val description: String,
    val isPreloaded: Boolean = true
)

@Entity(tableName = "note_recalls")
data class NoteRecallEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val snippet: String,
    val category: String = "Notice", // Prescription, Deadline, Financial, Personal
    val documentId: String? = null,
    val scheduledTimeFormatted: String = "Today, 6:00 PM",
    val isTriggered: Boolean = true,
    val isDone: Boolean = false,
    val accentColorHex: String = "#EBDBD3",
    val audioNoteText: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
