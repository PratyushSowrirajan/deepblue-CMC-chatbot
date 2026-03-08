package com.example.healthassistant.core.tts

interface TextToSpeechManager {
    fun speak(text: String)
    fun stop()
    fun shutdown()
}
