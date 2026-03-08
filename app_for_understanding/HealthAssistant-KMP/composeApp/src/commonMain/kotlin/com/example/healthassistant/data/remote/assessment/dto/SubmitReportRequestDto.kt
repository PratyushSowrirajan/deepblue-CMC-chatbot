package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubmitReportRequestDto(
    val session_id: String
)
