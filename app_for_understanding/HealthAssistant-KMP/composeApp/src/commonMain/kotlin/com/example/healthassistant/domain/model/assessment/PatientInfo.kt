package com.example.healthassistant.domain.model.assessment
import kotlinx.serialization.Serializable

@Serializable
data class PatientInfo(
    val name: String,
    val age: Int,
    val gender: String
)
