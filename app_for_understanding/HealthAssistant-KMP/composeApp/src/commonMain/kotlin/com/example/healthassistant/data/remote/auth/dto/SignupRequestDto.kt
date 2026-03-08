package com.example.healthassistant.data.remote.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignupRequestDto(
    val email: String,
    val password: String
)