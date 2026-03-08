package com.example.healthassistant.data.remote.bootstrap.dto

import com.example.healthassistant.data.remote.assessment.dto.ReportDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BootstrapResponseDto(

    val reports: List<ReportDto>,

    val profile: List<QuestionAnswerDto>,

    val medical: List<QuestionAnswerDto>
)

@Serializable
data class QuestionAnswerDto(
    val question_id: String,
    val question_text: String,
    val answer_json: JsonElement
)