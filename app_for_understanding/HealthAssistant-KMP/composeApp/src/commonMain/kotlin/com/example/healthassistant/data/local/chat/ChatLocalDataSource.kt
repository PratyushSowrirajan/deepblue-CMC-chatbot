package com.example.healthassistant.data.local.chat

import com.example.healthassistant.domain.model.chat.ChatMessage

interface ChatLocalDataSource {

    suspend fun insert(message: ChatMessage)

    suspend fun getMessages(sessionId: String): List<ChatMessage>

    suspend fun clear(sessionId: String)
}
