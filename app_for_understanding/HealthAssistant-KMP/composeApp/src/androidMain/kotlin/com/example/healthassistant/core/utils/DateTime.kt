package com.example.healthassistant.core.utils

import java.text.SimpleDateFormat
import java.util.*

actual object DateTime {

    actual fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    actual fun parseIsoToMillis(isoString: String): Long {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.parse(isoString)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}
