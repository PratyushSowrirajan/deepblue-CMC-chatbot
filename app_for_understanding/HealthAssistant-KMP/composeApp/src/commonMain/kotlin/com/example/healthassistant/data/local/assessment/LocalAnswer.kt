package com.example.healthassistant.data.local.assessment

data class LocalAnswer(
    val questionId: String,
    val questionText: String,
    val options: List<String>,
    val selectedAnswer: String
)