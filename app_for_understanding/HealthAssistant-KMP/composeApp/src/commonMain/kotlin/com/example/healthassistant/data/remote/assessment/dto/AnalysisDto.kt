package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnalysisDto(
    val urgency: String,
    val urgency_color: String,
    val headline: String,
    val advice: List<String>,
    val notes: List<String>? = null
)
