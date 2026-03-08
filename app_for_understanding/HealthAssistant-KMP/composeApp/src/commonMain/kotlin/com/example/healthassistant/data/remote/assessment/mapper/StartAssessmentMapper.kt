package com.example.healthassistant.data.remote.assessment.mapper

import com.example.healthassistant.data.remote.assessment.dto.StartAssessmentResponseDto
import com.example.healthassistant.domain.model.assessment.*

fun StartAssessmentResponseDto.toDomain(): AssessmentSession {
    return AssessmentSession(
        sessionId = session_id,
        question = Question(
            id = question.question_id,
            text = question.text,
            responseType = question.response_type,
            responseOptions = question.response_options?.map {
                ResponseOption(
                    id = it.id,
                    label = it.label
                )
            },
            isCompulsory = question.is_compulsory
        )
    )
}
