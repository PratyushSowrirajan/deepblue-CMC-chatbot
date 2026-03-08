package com.example.healthassistant.data.remote.chat.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageResponseDto(
    val message: String
)
