package com.example.healthassistant.stt

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.healthassistant.core.stt.SpeechToTextManager


class AndroidSpeechToTextManager(
    private val context: Context
) : SpeechToTextManager {



    private val recognizer: SpeechRecognizer =
        SpeechRecognizer.createSpeechRecognizer(context)

    private var listening = false

    override fun startListening(
        onResult: (String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        Log.d("STT", "startListening() ensure mic permission is granted")
        if (listening){
            Log.d("STT", "Already listening, ignoring")
            return
        }
        listening = true

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        }

        recognizer.setRecognitionListener(object : RecognitionListener {

            override fun onResults(results: android.os.Bundle) {
                listening = false

                val text = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()

                Log.d("STT", "Final recognized text: $text")

                text?.let { onResult(it) }
            }


            override fun onError(error: Int) {
                listening = false
                Log.e("STT", "STT error code: $error")
                onError(RuntimeException("STT error code: $error"))
            }

            override fun onReadyForSpeech(params: android.os.Bundle?) {}
            override fun onBeginningOfSpeech() {
                Log.d("STT", "User started speaking")
            }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: android.os.Bundle) {
                val text = partialResults
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()

                Log.d("STT", "Partial result: $text")
            }
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        })

        recognizer.startListening(intent)
    }

    override fun stopListening() {
        listening = false
        recognizer.stopListening()
    }

    override fun isListening(): Boolean = listening
}
