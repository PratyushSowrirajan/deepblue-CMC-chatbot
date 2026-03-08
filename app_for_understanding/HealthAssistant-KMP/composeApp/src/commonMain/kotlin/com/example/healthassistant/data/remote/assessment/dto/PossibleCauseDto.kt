package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class PossibleCauseDto(
    val id: String,
    val title: String,
    val short_description: String,
    val subtitle: String? = null,
    val severity: String,
    val probability: Double,
    val detail: CauseDetailDto
)
