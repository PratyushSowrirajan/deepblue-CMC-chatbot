package com.example.healthassistant.core.utils

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.healthassistant.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

lateinit var appContext: android.content.Context

actual fun shareNews(
    imageUrl: String?,
    appName: String,
    description: String,
    source: String
) {

    if (imageUrl.isNullOrBlank()) return

    CoroutineScope(Dispatchers.Main).launch {

        val imageLoader = ImageLoader(appContext)

        val request = ImageRequest.Builder(appContext)
            .data(imageUrl)
            .allowHardware(false)
            .build()

        val result = imageLoader.execute(request)
        val bitmap =
            (result.drawable as? BitmapDrawable)?.bitmap ?: return@launch

        val file = File(appContext.cacheDir, "shared_news.png")

        withContext(Dispatchers.IO) {
            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }

        val uri = FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.provider",
            file
        )

        val shareText = """
            $appName
            
            $description
            
            Source: $source
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, shareText)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Share via").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        appContext.startActivity(chooser)

    }
}
