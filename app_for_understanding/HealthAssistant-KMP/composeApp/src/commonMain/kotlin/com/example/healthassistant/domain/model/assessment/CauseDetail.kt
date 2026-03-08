package com.example.healthassistant.domain.model.assessment
import kotlinx.serialization.Serializable

@Serializable
data class CauseDetail(
    val aboutThis: List<String>,
    val percentage: Int,
    val commonDescription: String,
    val whatYouCanDoNow: List<String>,
    val warning: String?
)
