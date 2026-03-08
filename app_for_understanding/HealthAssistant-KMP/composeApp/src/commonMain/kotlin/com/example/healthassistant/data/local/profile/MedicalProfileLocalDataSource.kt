package com.example.healthassistant.data.local.profile

interface MedicalProfileLocalDataSource {

    suspend fun insert(
        questionId: String,
        questionText: String,
        answerJson: String
    )

    suspend fun clearAll()
}