package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubmitAnswerResponseDto(
    val status: String,
    val question: QuestionDto? = null
)