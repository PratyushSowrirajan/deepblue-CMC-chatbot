package com.example.healthassistant.data.remote.profile.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileAnswerDto(
    val type: String,
    val value: String? = null,
    val selected_option_label: String? = null,
    val number_value: Int? = null
)