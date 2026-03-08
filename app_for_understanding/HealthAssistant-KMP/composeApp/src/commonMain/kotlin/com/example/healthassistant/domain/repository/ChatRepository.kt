package com.example.healthassistant.domain.repository

import com.example.healthassistant.domain.model.chat.ChatMessage

interface ChatRepository {

    suspend fun startChat(
        currentReportId: String? = null
    ): ChatMessage

    suspend fun sendMessage(
        sessionId: String,
        userMessage: String
    ): ChatMessage

    suspend fun getMessages(sessionId: String): List<ChatMessage>

    suspend fun endChat(sessionId: String)
}