package com.example.healthassistant.data.remote.auth

import com.example.healthassistant.data.remote.auth.dto.*

interface AuthApi {

    suspend fun signup(
        request: SignupRequestDto
    ): AuthResponseDto

    suspend fun login(
        request: LoginRequestDto
    ): AuthResponseDto
}