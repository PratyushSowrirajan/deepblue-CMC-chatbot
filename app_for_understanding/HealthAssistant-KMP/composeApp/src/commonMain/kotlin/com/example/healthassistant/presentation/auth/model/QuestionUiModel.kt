package com.example.healthassistant.presentation.auth.model

data class QuestionUiModel(

    val id: String,

    val questionText: String,

    val type: String,

    val options: List<String> = emptyList(),

    val value: String? = null,

    val required: Boolean = true
)