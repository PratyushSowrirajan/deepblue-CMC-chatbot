package com.example.healthassistant.data.remote.assessment

import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.data.remote.assessment.dto.AnswerDto
import com.example.healthassistant.data.remote.assessment.dto.ReportDto
import com.example.healthassistant.data.remote.assessment.dto.StartAssessmentResponseDto
import com.example.healthassistant.data.remote.assessment.dto.SubmitAnswerRequestDto
import com.example.healthassistant.data.remote.assessment.dto.SubmitAnswerResponseDto
import com.example.healthassistant.data.remote.assessment.dto.SubmitReportRequestDto
import com.example.healthassistant.data.remote.bootstrap.dto.BootstrapResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.serialization.encodeToString

class AssessmentApiImpl(
    private val client: HttpClient,
    private val baseUrl: String
) : AssessmentApi {

    override suspend fun startAssessment(): StartAssessmentResponseDto {

        AppLogger.d("API", "GET /assessment/start")

        val response =
            client.get("$baseUrl/assessment/start").body<StartAssessmentResponseDto>()

        AppLogger.d(
            "API",
            "Response received → session_id=${response.session_id}"
        )

        return response
    }

    override suspend fun submitAnswer(
        sessionId: String,
        questionId: String,
        questionText: String,
        answer: AnswerDto,
        imageBytes: ByteArray?,
        imageFileName: String?
    ): SubmitAnswerResponseDto {

        return if (imageBytes == null) {

            // 🔹 EXACT OLD JSON CONTRACT (UNCHANGED)
            client.post("$baseUrl/assessment/answer") {
                contentType(ContentType.Application.Json)
                setBody(
                    SubmitAnswerRequestDto(
                        session_id = sessionId,
                        question_id = questionId,
                        question_text = questionText,
                        answer_json = answer
                    )
                )
            }.body()

        } else {

            // 🔥 MULTIPART FLOW (ONLY HERE WE ENCODE)
            client.post("$baseUrl/assessment/answer") {

                setBody(
                    io.ktor.client.request.forms.MultiPartFormDataContent(
                        formData {
                            append("session_id", sessionId)
                            append("question_id", questionId)
                            append("question_text", questionText)

                            // 🔥 Encode ONLY for multipart
                            append(
                                "answer_json",
                                Json.encodeToString(answer)
                            )

                            append(
                                key = "image",
                                value = imageBytes,
                                headers = io.ktor.http.Headers.build {
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "form-data; name=\"image\"; filename=\"${imageFileName ?: "image.jpg"}\""
                                    )
                                    append(
                                        HttpHeaders.ContentType,
                                        "image/jpeg"
                                    )
                                }
                            )
                        }
                    )
                )
            }.body()
        }
    }

    override suspend fun submitReport(
        request: SubmitReportRequestDto
    ): ReportDto {

        AppLogger.d("API", "POST /assessment/report → session_id=${request.session_id}")

        return client.post("$baseUrl/assessment/report") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun endSession(sessionId: String) {

        AppLogger.d("API", "POST /assessment/end → $sessionId")

        client.post("$baseUrl/assessment/end") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("session_id" to sessionId))
        }
    }

    override suspend fun getUserReports(): List<ReportDto> {

        AppLogger.d("API", "GET /user/reports")

        return client.get("$baseUrl/user/reports") {
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getBootstrap(): BootstrapResponseDto {

        AppLogger.d("API", "GET /user/bootstrap")

        return client.get("$baseUrl/user/bootstrap") {
            contentType(ContentType.Application.Json)
        }.body()
    }
}