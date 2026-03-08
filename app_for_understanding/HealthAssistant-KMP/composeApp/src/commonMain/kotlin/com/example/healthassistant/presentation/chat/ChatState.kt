package com.example.healthassistant.presentation.chat

import com.example.healthassistant.domain.model.chat.ChatMessage

data class ChatState(
    val isLoading: Boolean = false,
    val sessionId: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val typedMessage: String = "",
    val error: String? = null,
    val isListening: Boolean = false,   // 🎤
    val isTtsEnabled: Boolean = true    // 🔊
)
