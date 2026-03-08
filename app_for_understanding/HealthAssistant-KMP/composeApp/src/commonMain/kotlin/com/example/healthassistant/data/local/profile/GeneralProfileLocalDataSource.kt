package com.example.healthassistant.data.local.profile

import com.example.healthassistant.data.remote.assessment.dto.AnswerDto

interface GeneralProfileLocalDataSource {

    suspend fun insert(
        questionId: String,
        questionText: String,
        answerJson: String
    )

    suspend fun clearAll()
}