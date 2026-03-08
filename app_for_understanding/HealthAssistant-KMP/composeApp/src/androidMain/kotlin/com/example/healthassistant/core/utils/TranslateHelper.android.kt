package com.example.healthassistant.core.utils

import com.example.healthassistant.core.translator.AutoTranslator

actual suspend fun platformTranslate(text: String): String {
    return AutoTranslator.translate(text)
}

actual fun platformInitTranslator(lang: String) {
    AutoTranslator.init(lang)
}