package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubmitAnswerRequestDto(
    val session_id: String,
    val question_id: String,
    val question_text: String,
    val answer_json: AnswerDto
)
