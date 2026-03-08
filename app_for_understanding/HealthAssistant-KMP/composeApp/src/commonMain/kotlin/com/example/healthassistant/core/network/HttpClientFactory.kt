package com.example.healthassistant.core.network

import io.ktor.client.HttpClient

expect fun createHttpClient(): HttpClient
