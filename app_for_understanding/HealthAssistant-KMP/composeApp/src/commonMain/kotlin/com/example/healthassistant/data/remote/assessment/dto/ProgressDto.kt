package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProgressDto(
    val current: Int,
    val total: Int
)