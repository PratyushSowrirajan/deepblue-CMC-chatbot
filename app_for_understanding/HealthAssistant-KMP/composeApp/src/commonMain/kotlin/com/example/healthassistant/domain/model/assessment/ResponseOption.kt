package com.example.healthassistant.domain.model.assessment

import kotlinx.serialization.Serializable

@Serializable
data class ResponseOption(
    val id: String,
    val label: String
)
