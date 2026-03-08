package com.example.healthassistant.core.utils


import platform.Foundation.*

actual object DateTime {

    actual fun getCurrentTimeMillis(): Long {
        return (NSDate().timeIntervalSince1970 * 1000).toLong()
    }

    actual fun parseIsoToMillis(isoString: String): Long {
        val formatter = NSDateFormatter().apply {
            dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"
            timeZone = NSTimeZone.timeZoneWithAbbreviation("UTC")
        }

        val date = formatter.dateFromString(isoString)
        return ((date?.timeIntervalSince1970 ?: 0.0) * 1000).toLong()
    }
}
