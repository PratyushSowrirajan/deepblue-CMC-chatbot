package com.example.healthassistant.data.remote.chat.dto

import com.example.healthassistant.data.remote.assessment.dto.PatientInfoDto
import com.example.healthassistant.data.remote.assessment.dto.PossibleCauseDto
import kotlinx.serialization.Serializable

@Serializable
data class ReportDto(
    val report_id: String,
    val generated_at: String,
    val urgency_level: String,
    val summary: List<String>,
    val patient_info: PatientInfoDto?,
    val possible_causes: List<PossibleCauseDto>,
    val advice: List<String>
)
