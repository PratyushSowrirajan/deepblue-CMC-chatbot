package com.example.healthassistant.data.repository

import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.data.local.profile.GeneralProfileLocalDataSource
import com.example.healthassistant.data.local.profile.MedicalProfileLocalDataSource
import com.example.healthassistant.data.local.profile.ProfileLocalDataSource
import com.example.healthassistant.data.local.report.ReportLocalDataSource
//import com.example.healthassistant.data.local.assessment.AssessmentLocalDataSource
import com.example.healthassistant.data.remote.assessment.AssessmentApi
import com.example.healthassistant.data.remote.assessment.dto.AnswerDto
import com.example.healthassistant.data.remote.assessment.dto.AnswerRequestDto
import com.example.healthassistant.data.remote.assessment.dto.AnswerValueDto
import com.example.healthassistant.data.remote.assessment.dto.ContextRequestDto
import com.example.healthassistant.data.remote.assessment.dto.QuestionDto
import com.example.healthassistant.data.remote.assessment.dto.ResponseOptionDto
import com.example.healthassistant.data.remote.assessment.dto.SubmitAnswerRequestDto
import com.example.healthassistant.data.remote.assessment.dto.SubmitReportRequestDto
import com.example.healthassistant.data.remote.assessment.mapper.toDomain
import com.example.healthassistant.data.remote.bootstrap.BootstrapApi
import com.example.healthassistant.domain.model.assessment.AssessmentSession
import com.example.healthassistant.domain.model.assessment.Question
import com.example.healthassistant.domain.model.assessment.Report
import com.example.healthassistant.domain.repository.AssessmentRepository
import kotlinx.serialization.encodeToString

class AssessmentRepositoryImpl(
    private val api: AssessmentApi,
    private val bootstrapApi: BootstrapApi,
    private val profileLocal: ProfileLocalDataSource,
    private val reportLocal: ReportLocalDataSource,
    private val generalProfileLocal: GeneralProfileLocalDataSource,
    private val medicalProfileLocal: MedicalProfileLocalDataSource
) : AssessmentRepository {

    private var storedAnswersMap: Map<String, AnswerDto> = emptyMap()

    private var currentSessionId: String = ""


    override suspend fun startAssessment(): AssessmentSession {

        AppLogger.d("REPO", "startAssessment() called")

        val dto = api.startAssessment()

        currentSessionId = dto.session_id

        // 🔥 STORE STORED ANSWERS IN MEMORY
        storedAnswersMap = dto.stored_answers.associate {
            it.question_id to it.answer_json
        }

        AppLogger.d("REPO", "Stored Answers Loaded → ${storedAnswersMap.size}")

        return dto.toDomain()
    }

    override suspend fun submitAnswer(
        question: Question,
        answer: AnswerDto,
        imageBytes: ByteArray?,
        imageFileName: String?
    ): AssessmentSession? {

        imageBytes?.let {

            AppLogger.d(
                "IMAGE_UPLOAD",
                "Image size bytes: ${it.size}"
            )

            AppLogger.d(
                "IMAGE_UPLOAD",
                "Image size KB: ${it.size / 1024}"
            )
        }

        val finalImageBytes = imageBytes?.let {

            val maxSize = 50 * 1024 * 1024   // 50MB

            if (it.size <= maxSize) {
                it
            } else {

                AppLogger.d(
                    "IMAGE_UPLOAD",
                    "Image larger than 50MB → compressing"
                )

                compressImageBytes(it)
            }
        }


        val response = api.submitAnswer(
            sessionId = currentSessionId,
            questionId = question.id,
            questionText = question.text,
            answer = answer,
            imageBytes = finalImageBytes,
            imageFileName = imageFileName
        )

        return if (response.status == "completed") {
            null
        } else {
            AssessmentSession(
                sessionId = currentSessionId,
                question = response.question!!.toDomain()
            )
        }
    }


    override suspend fun submitFinalReport(): Report {

        val request = SubmitReportRequestDto(
            session_id = currentSessionId
        )

        AppLogger.d("REPO", "Generating report for session → $currentSessionId")

        val reportDto = api.submitReport(request)

        val report = reportDto.toDomain()

        reportLocal.insert(report)

        return report
    }
    override suspend fun getStoredAnswer(questionId: String): AnswerDto? {
        return storedAnswersMap[questionId]
    }

    override suspend fun getAllReports(): List<Report> {
        return reportLocal.getAll()
    }

    override suspend fun getReportById(id: String): Report? {
        return reportLocal.getById(id)
    }


    override suspend fun getProfileAnswer(questionId: String): AnswerDto? {
        return profileLocal.getAnswer(questionId)
    }

    override suspend fun endSession() {
        if (currentSessionId.isNotEmpty()) {
            AppLogger.d("REPO", "ENDING SESSION → $currentSessionId")
            api.endSession(currentSessionId)
            currentSessionId = ""
            storedAnswersMap = emptyMap()
        }
    }

    override suspend fun syncReports() {

        AppLogger.d("REPO", "Syncing reports from server")

        val reports = api.getUserReports()

        reportLocal.clearAll()

        reports
            .map { it.toDomain() }
            .forEach { reportLocal.insert(it) }

        AppLogger.d("REPO", "Reports synced → ${reports.size}")
    }

    override suspend fun bootstrapSync() {

        AppLogger.d("REPO", "Bootstrap syncing user data")

        val response = bootstrapApi.getBootstrap()

        // REPORTS
        reportLocal.clearAll()

        response.reports
            .map { it.toDomain() }
            .forEach { reportLocal.insert(it) }

        AppLogger.d("REPO", "Reports synced → ${response.reports.size}")

        // PROFILE
        generalProfileLocal.clearAll()

        response.profile.forEach {

            generalProfileLocal.insert(
                questionId = it.question_id,
                questionText = it.question_text,
                answerJson = it.answer_json.toString()
            )
        }

        AppLogger.d("REPO", "Profile answers synced → ${response.profile.size}")

        // MEDICAL
        medicalProfileLocal.clearAll()

        response.medical.forEach {

            medicalProfileLocal.insert(
                questionId = it.question_id,
                questionText = it.question_text,
                answerJson = it.answer_json.toString()
            )
        }

        AppLogger.d("REPO", "Medical answers synced → ${response.medical.size}")
    }

    private fun compressImageBytes(bytes: ByteArray): ByteArray {

        val maxSize = 50 * 1024 * 1024

        if (bytes.size <= maxSize) return bytes

        var newSize = maxSize

        AppLogger.d(
            "IMAGE_UPLOAD",
            "Reducing image size from ${bytes.size} to $newSize"
        )

        return bytes.copyOf(newSize)
    }

}