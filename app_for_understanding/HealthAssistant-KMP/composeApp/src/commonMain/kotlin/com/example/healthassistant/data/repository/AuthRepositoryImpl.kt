package com.example.healthassistant.data.repository

import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.data.remote.auth.AuthApi
import com.example.healthassistant.data.remote.auth.dto.*
import com.example.healthassistant.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: AuthApi
) : AuthRepository {

    override suspend fun signup(
        email: String,
        password: String
    ): AuthResponseDto {

        AppLogger.section("AUTH_REPO", "Signup Called")

        return api.signup(
            SignupRequestDto(email, password)
        )
    }

    override suspend fun login(
        email: String,
        password: String
    ): AuthResponseDto {

        AppLogger.section("AUTH_REPO", "Login Called")

        return api.login(
            LoginRequestDto(email, password)
        )
    }
}