package com.example.healthassistant.data.remote.chat.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileItemDto(
    val question: String,
    val answer: String
)