package com.example.healthassistant.core.platform


import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(onBack: () -> Unit) {
    BackHandler {
        onBack()
    }
}