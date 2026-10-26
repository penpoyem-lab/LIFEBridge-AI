package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val sender: String, // "USER" or "ASSISTANT"
    val timestamp: Long = System.currentTimeMillis(),
    val actionLabel: String? = null,
    val actionRoute: String? = null,
    val isError: Boolean = false
)
