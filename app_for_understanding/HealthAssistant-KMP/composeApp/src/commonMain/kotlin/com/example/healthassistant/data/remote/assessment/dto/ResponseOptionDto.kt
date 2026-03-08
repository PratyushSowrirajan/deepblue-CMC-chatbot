package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResponseOptionDto(
    val id: String,
    val label: String
)
