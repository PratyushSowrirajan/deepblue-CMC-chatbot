package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class ContextRequestDto(
    val session_id: String,
    val user_choice: String, // "new_user" | "existing_user"
    val questionnaire_context: Map<String, String>? = null,
    val medical_report: MedicalReportDto? = null
)

@Serializable
data class MedicalReportDto(
    val report_id: String,
    val summary: String,
    val last_urgency: String
)
