package com.example.healthassistant.data.remote.chat

import com.example.healthassistant.data.remote.chat.dto.*

interface ChatApi {

    suspend fun startChat(
        request: ChatStartRequestDto
    ): ChatStartResponseDto


    suspend fun sendMessage(
        request: ChatMessageRequestDto
    ): ChatMessageResponseDto

    suspend fun endChat(sessionId: String)
}
