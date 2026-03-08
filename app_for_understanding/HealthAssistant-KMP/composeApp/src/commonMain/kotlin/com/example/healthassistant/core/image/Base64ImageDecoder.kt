package com.example.healthassistant.core.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

@Composable
expect fun decodeBase64ToImageBitmap(base64: String): ImageBitmap?