package com.example.healthassistant.data.remote.profile.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponseDto(
    val success: Boolean,
    val message: String
)