package com.example.healthassistant.core.platform

class JVMPlatform : Platform {
    override val name: String = "Desktop JVM"
}

actual fun getPlatform(): Platform = JVMPlatform()
