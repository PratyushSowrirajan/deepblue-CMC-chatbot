package com.example.healthassistant.data.local.assessment

data class LocalContext(
    val questionId: String,
    val questionText: String,
    val responseType: String,
    val responseOptionsJson: String?,
    val answerJson: String
)
