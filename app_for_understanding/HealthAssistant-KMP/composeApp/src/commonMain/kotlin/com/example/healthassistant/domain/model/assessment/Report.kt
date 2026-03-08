package com.example.healthassistant.domain.model.assessment

import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val reportId: String,
    val topic: String,
    val generatedAt: String,
    val summary: List<String>,
    val possibleCauses: List<PossibleCause>,
    val advice: List<String>,
    val urgencyLevel: String,
    val patientInfo: PatientInfo? = null
)
