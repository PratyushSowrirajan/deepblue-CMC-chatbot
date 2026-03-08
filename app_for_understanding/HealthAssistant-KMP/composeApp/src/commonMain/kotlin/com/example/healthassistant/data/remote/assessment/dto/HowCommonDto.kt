package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class HowCommonDto(
    val percentage: Int,
    val description: String
)
