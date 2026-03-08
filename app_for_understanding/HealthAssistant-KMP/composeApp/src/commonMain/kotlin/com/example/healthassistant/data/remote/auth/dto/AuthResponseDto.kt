package com.example.healthassistant.data.remote.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    val success: Boolean,
    val message: String,
    val token: String? = null
)