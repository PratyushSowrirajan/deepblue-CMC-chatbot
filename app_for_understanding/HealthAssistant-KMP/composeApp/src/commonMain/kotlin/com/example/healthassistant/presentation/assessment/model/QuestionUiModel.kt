package com.example.healthassistant.presentation.assessment.model

data class QuestionUiModel(
    val id: String,
    val title: String,
    val step: Int,
    val totalSteps: Int,
    val question: String,
    val options: List<AnswerUiModel>
)
