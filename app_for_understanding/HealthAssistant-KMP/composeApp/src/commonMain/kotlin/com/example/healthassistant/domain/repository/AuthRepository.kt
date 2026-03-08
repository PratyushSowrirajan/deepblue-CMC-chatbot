package com.example.healthassistant.domain.repository

import com.example.healthassistant.data.remote.auth.dto.AuthResponseDto

interface AuthRepository {

    suspend fun signup(
        email: String,
        password: String
    ): AuthResponseDto

    suspend fun login(
        email: String,
        password: String
    ): AuthResponseDto
}