package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnswerDto(
    val type: String,
    val value: String? = null,
    val selected_option_id: String? = null,
    val selected_option_label: String? = null,
    val selected_option_ids: List<String>? = null,
    val selected_option_labels: List<String>? = null
)
