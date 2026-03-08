package com.example.healthassistant.data.remote.chat.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDataDto(
    val question: String,
    val answer: String
)