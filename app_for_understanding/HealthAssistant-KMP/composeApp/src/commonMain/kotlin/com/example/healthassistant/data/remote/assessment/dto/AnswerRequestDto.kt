package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnswerRequestDto(
    val session_id: String,
    val phase: String,
    val question_id: String,
    val answer: AnswerValueDto
)
