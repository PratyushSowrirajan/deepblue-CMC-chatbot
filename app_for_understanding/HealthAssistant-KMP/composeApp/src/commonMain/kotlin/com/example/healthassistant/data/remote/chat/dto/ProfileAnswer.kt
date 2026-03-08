package com.example.healthassistant.data.remote.chat.dto
import kotlinx.serialization.Serializable

@Serializable
data class ProfileAnswer(
    val questionId: String,
    val questionText: String,
    val answerText: String
)
