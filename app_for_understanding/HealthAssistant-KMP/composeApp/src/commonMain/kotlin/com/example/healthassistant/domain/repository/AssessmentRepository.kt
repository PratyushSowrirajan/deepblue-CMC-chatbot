package com.example.healthassistant.domain.repository

import com.example.healthassistant.data.remote.assessment.dto.AnswerDto
import com.example.healthassistant.domain.model.assessment.AssessmentSession
import com.example.healthassistant.domain.model.assessment.Question
import com.example.healthassistant.domain.model.assessment.Report

interface AssessmentRepository {

    suspend fun startAssessment(): AssessmentSession

    suspend fun submitAnswer(
        question: Question,
        answer: AnswerDto,
        imageBytes: ByteArray? = null,
        imageFileName: String? = null
    ): AssessmentSession?


    suspend fun submitFinalReport(): Report

    suspend fun getProfileAnswer(questionId: String): AnswerDto?
    suspend fun getStoredAnswer(questionId: String): AnswerDto?

    suspend fun getAllReports(): List<Report>
    suspend fun getReportById(id: String): Report?

    suspend fun endSession()

    suspend fun syncReports()

    suspend fun bootstrapSync()



}


