package com.example.healthassistant.stt

import com.example.healthassistant.core.stt.SpeechToTextManager
import org.vosk.Model
import org.vosk.Recognizer
import javax.sound.sampled.*

class DesktopSpeechToTextManager(
    modelPath: String
) : SpeechToTextManager {

    private val model = Model(modelPath)
    private val recognizer = Recognizer(model, 16000f)
    private var listening = false
    private var thread: Thread? = null

    override fun startListening(
        onResult: (String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (listening) return
        listening = true

        thread = Thread {
            try {
                val format = AudioFormat(16000f, 16, 1, true, false)
                val info = DataLine.Info(TargetDataLine::class.java, format)
                val microphone = AudioSystem.getLine(info) as TargetDataLine

                microphone.open(format)
                microphone.start()

                val buffer = ByteArray(4096)

                while (listening) {
                    val bytesRead = microphone.read(buffer, 0, buffer.size)
                    if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                        val result = recognizer.result
                        val text = extractText(result)
                        if (text.isNotBlank()) {
                            onResult(text)
                            break
                        }
                    }
                }

                microphone.stop()
                microphone.close()

            } catch (e: Exception) {
                onError(e)
            } finally {
                listening = false
            }
        }

        thread?.start()
    }

    override fun stopListening() {
        listening = false
        thread?.interrupt()
    }

    override fun isListening(): Boolean = listening

    private fun extractText(json: String): String {
        // Very simple extraction
        return json.substringAfter("\"text\" : \"")
            .substringBefore("\"")
            .trim()
    }
}
