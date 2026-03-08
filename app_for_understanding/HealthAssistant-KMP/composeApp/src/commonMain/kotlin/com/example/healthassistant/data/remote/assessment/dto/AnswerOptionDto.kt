package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnswerOptionDto(
    val id: String,
    val label: String
)
