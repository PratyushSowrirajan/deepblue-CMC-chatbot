package com.example.healthassistant.core.platform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform