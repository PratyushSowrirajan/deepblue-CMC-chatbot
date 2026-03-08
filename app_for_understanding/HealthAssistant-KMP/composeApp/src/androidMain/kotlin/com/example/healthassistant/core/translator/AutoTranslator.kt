package com.example.healthassistant.core.translator

import com.google.mlkit.nl.translate.*
import kotlinx.coroutines.tasks.await

object AutoTranslator {

    private var translator: Translator? = null

    fun init(targetLang: String) {

        if (targetLang == "en") {
            translator = null
            return
        }

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(targetLang)
            .build()

        translator = Translation.getClient(options)

        translator?.downloadModelIfNeeded()
    }

    suspend fun translate(text: String): String {
        return try {
            translator?.translate(text)?.await() ?: text
        } catch (e: Exception) {
            text
        }
    }
}