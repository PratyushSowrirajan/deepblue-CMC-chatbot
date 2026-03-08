package com.example.healthassistant.data.remote.assessment

import com.example.healthassistant.data.remote.assessment.dto.AnswerDto
import com.example.healthassistant.data.remote.assessment.dto.ReportDto
import com.example.healthassistant.data.remote.assessment.dto.StartAssessmentResponseDto
import com.example.healthassistant.data.remote.assessment.dto.SubmitAnswerRequestDto
import com.example.healthassistant.data.remote.assessment.dto.SubmitAnswerResponseDto
import com.example.healthassistant.data.remote.assessment.dto.SubmitReportRequestDto
import com.example.healthassistant.data.remote.bootstrap.dto.BootstrapResponseDto

interface AssessmentApi {

    suspend fun startAssessment(): StartAssessmentResponseDto

    suspend fun submitAnswer(
        sessionId: String,
        questionId: String,
        questionText: String,
        answer: AnswerDto,
        imageBytes: ByteArray? = null,
        imageFileName: String? = null
    ): SubmitAnswerResponseDto

    suspend fun submitReport(
        request: SubmitReportRequestDto
    ): ReportDto

    suspend fun endSession(sessionId: String)

    suspend fun getUserReports(): List<ReportDto>

    suspend fun getBootstrap(): BootstrapResponseDto
}