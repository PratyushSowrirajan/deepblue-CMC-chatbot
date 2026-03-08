package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class ActionPromptDto(
    val question: String,
    val options: List<AnswerOptionDto>
)
