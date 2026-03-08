package com.example.healthassistant

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Health Assistant Booth"
    ) {
        DesktopApp()
    }
}
