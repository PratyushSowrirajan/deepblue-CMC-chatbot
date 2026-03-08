package com.example.healthassistant.data.remote.chat.dto

import kotlinx.serialization.Serializable
import com.example.healthassistant.data.remote.assessment.dto.ReportResponseDto

@Serializable
data class ReportWrapperDto(
    val report_id: String,
    val generated_at: String,
    val is_main: Boolean,
    val report_data: ReportResponseDto
)