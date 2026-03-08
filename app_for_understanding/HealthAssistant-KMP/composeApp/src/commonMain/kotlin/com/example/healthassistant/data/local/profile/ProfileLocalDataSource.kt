package com.example.healthassistant.data.local.profile

import com.example.healthassistant.data.remote.assessment.dto.AnswerDto
import com.example.healthassistant.data.remote.chat.dto.ProfileAnswer
import com.example.healthassistant.domain.model.assessment.Question

interface ProfileLocalDataSource {

    suspend fun insertOrUpdate(
        question: Question,
        answer: AnswerDto
    )

    suspend fun getAnswer(questionId: String): AnswerDto?

    suspend fun getAll(): List<ProfileAnswer>
}

