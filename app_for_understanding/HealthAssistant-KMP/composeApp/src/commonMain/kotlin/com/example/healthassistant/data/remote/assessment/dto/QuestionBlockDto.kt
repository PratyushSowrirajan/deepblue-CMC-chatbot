package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class QuestionBlockDto(
    val question_id: String,
    val text: String,
    val type: String,
    val input_mode: String? = null,
    val input_hint: String? = null
)