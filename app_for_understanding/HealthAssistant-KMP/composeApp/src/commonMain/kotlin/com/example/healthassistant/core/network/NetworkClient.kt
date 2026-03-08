package com.example.healthassistant.core.network

import com.example.healthassistant.core.auth.TokenManager
import io.ktor.client.*
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object NetworkClient {

    val httpClient = HttpClient {

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                }
            )
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 120_000   // 2 minutes
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 120_000
        }

        // 🔥 GLOBAL JWT INJECTION
        install(DefaultRequest) {

            header(HttpHeaders.ContentType, ContentType.Application.Json)

            val token = TokenManager.getToken()

            if (!token.isNullOrBlank()) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
}