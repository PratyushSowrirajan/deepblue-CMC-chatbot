package com.example.healthassistant.data.remote.auth

import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.data.remote.auth.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApiImpl(
    private val client: HttpClient,
    private val baseUrl: String
) : AuthApi {

    override suspend fun signup(
        request: SignupRequestDto
    ): AuthResponseDto {

        AppLogger.section("AUTH_API", "SIGNUP REQUEST")

        AppLogger.logJson("AUTH_API", "REQUEST BODY", request)

        val response = client.post("$baseUrl/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        val body = response.body<AuthResponseDto>()

        AppLogger.logJson("AUTH_API", "RESPONSE BODY", body)

        return body
    }

    override suspend fun login(
        request: LoginRequestDto
    ): AuthResponseDto {

        AppLogger.section("AUTH_API", "LOGIN REQUEST")

        AppLogger.logJson("AUTH_API", "REQUEST BODY", request)

        val response = client.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        val body = response.body<AuthResponseDto>()

        AppLogger.logJson("AUTH_API", "RESPONSE BODY", body)

        return body
    }
}