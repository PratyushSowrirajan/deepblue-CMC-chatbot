package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnswerValueDto(
    val type: String,   // "option" | "text"
    val value: String
)
