package com.example.healthassistant.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import com.example.healthassistant.core.tts.TextToSpeechManager
import java.util.Locale

class AndroidTextToSpeechManager(
    context: Context
) : TextToSpeechManager {

    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
    }

    override fun speak(text: String) {
        tts?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "TTS_ID"
        )
    }

    override fun stop() {
        tts?.stop()
    }

    override fun shutdown() {
        tts?.shutdown()
    }
}
