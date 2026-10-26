package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "security_audit_logs")
data class AuditLogEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTime: String,
    val action: String, // e.g., "DOCUMENT_ACCESS", "PDF_COMPLIANCE_EXPORT", "VAULT_BIOMETRIC_AUTH", "TAMPER_CHECK"
    val targetTitle: String,
    val integrityHash: String, // SHA-256 or security token
    val severity: String = "INFO" // "INFO", "WARNING", "CRITICAL"
)
