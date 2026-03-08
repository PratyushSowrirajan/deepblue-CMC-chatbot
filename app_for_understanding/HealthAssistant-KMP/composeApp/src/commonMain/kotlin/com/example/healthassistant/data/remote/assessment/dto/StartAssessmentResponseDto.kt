package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class StartAssessmentResponseDto(
    val session_id: String,
    val question: QuestionDto,
    val stored_answers: List<StoredAnswerItemDto> = emptyList()
)

@Serializable
data class StoredAnswerItemDto(
    val question_id: String,
    val answer_json: AnswerDto
)