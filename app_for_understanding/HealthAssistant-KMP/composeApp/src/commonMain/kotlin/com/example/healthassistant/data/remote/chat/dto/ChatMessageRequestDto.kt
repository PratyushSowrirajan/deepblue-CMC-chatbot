package com.example.healthassistant.data.remote.chat.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageRequestDto(
    val session_id: String,
    val message: String
)

@Serializable
data class ChatHistoryDto(
    val role: String,   // "user" or "assistant"
    val content: String
)
