package com.example.healthassistant.data.remote.chat.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatStartResponseDto(
    val session_id: String,
    val message: String
)