package com.example.healthassistant.core.utils

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun encodeImageToBase64(bytes: ByteArray): String {
    return Base64.encode(bytes)
}