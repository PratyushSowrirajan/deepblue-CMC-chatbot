package com.example.healthassistant.core.utils

import androidx.compose.runtime.*

expect suspend fun platformTranslate(text: String): String
expect fun platformInitTranslator(lang: String)

@Composable
fun t(text: String): String {

    val language = LanguageState.currentLanguage.value
    var translated by remember(text, language) { mutableStateOf(text) }

    LaunchedEffect(text, language) {
        if (language == "en") {
            translated = text
        } else {
            translated = platformTranslate(text)
        }
    }

    return translated
}