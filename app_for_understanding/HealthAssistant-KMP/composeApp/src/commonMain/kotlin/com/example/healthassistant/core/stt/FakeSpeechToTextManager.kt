package com.example.healthassistant.core.stt

class FakeSpeechToTextManager : SpeechToTextManager {

    override fun startListening(
        onResult: (String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        // Simulate speech recognition
        onResult("I have pain from morning")
    }

    override fun stopListening() {
        // no-op
    }

    override fun isListening(): Boolean = false
}
