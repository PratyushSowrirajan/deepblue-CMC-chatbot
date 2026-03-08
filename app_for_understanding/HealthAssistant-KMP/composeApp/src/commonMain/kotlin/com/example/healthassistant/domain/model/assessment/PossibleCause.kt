package com.example.healthassistant.domain.model.assessment
import kotlinx.serialization.Serializable

@Serializable
data class PossibleCause(
    val id: String,
    val title: String,
    val shortDescription: String,
    val subtitle: String?,
    val severity: String,
    val probability: Double,
    val detail: CauseDetail
)
