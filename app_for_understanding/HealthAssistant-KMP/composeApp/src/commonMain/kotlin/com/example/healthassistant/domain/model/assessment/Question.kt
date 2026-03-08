package com.example.healthassistant.domain.model.assessment

data class Question(
    val id: String,
    val text: String,
    val responseType: String,
    val responseOptions: List<ResponseOption>?,
    val isCompulsory: Boolean
)
