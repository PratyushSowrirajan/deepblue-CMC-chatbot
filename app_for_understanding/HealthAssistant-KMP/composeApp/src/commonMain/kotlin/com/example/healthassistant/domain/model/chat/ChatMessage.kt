package com.example.healthassistant.domain.model.chat

data class ChatMessage(
    val id: String,
    val sessionId: String,
    val role: Role,
    val content: String,
    val timestamp: Long     // ISO string from backend
)

enum class Role {
    USER,
    ASSISTANT
}
