package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserReportsResponseDto(
    val reports: List<ReportResponseDto>
)