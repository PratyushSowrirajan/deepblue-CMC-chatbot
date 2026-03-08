package com.example.healthassistant.data.remote.bootstrap

import com.example.healthassistant.data.remote.bootstrap.dto.BootstrapResponseDto

interface BootstrapApi {

    suspend fun getBootstrap(): BootstrapResponseDto
}