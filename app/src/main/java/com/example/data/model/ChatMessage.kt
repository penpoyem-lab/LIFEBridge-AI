package com.example.data.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val sender: MessageSender,
    val timestamp: Long = System.currentTimeMillis(),
    val actionLabel: String? = null,
    val actionRoute: String? = null,
    val isError: Boolean = false
)

enum class MessageSender {
    USER,
    ASSISTANT
}
