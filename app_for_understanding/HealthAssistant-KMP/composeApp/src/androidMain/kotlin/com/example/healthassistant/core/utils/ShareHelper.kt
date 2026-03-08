package com.example.healthassistant.core.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ShareHelper {

    suspend fun shareNews(
        context: Context,
        imageUrl: String?,
        appName: String,
        description: String,
        source: String
    ) {
        if (imageUrl.isNullOrBlank()) return

        val imageLoader = ImageLoader(context)

        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false)
            .build()

        val result = imageLoader.execute(request)
        val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
            ?: return

        val file = File(context.cacheDir, "shared_news.png")

        withContext(Dispatchers.IO) {
            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
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

        context.startActivity(Intent.createChooser(intent, "Share via"))
    }
}