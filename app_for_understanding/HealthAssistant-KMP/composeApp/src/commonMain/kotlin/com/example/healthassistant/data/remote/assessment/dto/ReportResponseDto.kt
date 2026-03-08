package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReportResponseDto(
    val report_id: String,
    val assessment_topic: String,
    val generated_at: String,
    val patient_info: PatientInfoDto,
    val summary: List<String>,
    val possible_causes: List<PossibleCauseDto>,
    val advice: List<String>,
    val urgency_level: String
)

