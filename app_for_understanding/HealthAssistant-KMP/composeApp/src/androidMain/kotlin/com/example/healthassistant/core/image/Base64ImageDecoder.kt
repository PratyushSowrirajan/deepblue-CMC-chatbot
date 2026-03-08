package com.example.healthassistant.core.image

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

@Composable
actual fun decodeBase64ToImageBitmap(base64: String): ImageBitmap? {

    return try {

        val bytes = Base64.decode(base64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        bitmap?.asImageBitmap()

    } catch (e: Exception) {
        null
    }
}