package com.example.healthassistant.core.utils


expect object DateTime {

    fun getCurrentTimeMillis(): Long

    fun parseIsoToMillis(isoString: String): Long

}
