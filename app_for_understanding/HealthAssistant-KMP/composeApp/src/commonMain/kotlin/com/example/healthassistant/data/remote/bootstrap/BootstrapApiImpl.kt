package com.example.healthassistant.data.remote.bootstrap

import com.example.healthassistant.core.logger.AppLogger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import com.example.healthassistant.data.remote.bootstrap.dto.BootstrapResponseDto

class BootstrapApiImpl(
    private val client: HttpClient,
    private val baseUrl: String
) : BootstrapApi {

    override suspend fun getBootstrap(): BootstrapResponseDto {
        AppLogger.d("API", "GET /user/bootstrap")

        return client.get("$baseUrl/user/bootstrap").body()
    }
}