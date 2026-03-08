package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class QuestionDto(
    val question_id: String,
    val text: String,
    val response_type: String,
    val response_options: List<ResponseOptionDto>? = null,
    val is_compulsory: Boolean
)
