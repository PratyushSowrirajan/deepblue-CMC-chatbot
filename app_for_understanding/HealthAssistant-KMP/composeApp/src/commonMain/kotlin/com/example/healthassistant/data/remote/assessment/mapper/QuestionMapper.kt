package com.example.healthassistant.data.remote.assessment.mapper

import com.example.healthassistant.data.remote.assessment.dto.QuestionDto
import com.example.healthassistant.domain.model.assessment.Question
import com.example.healthassistant.domain.model.assessment.ResponseOption

fun QuestionDto.toDomain(): Question {
    return Question(
        id = question_id,
        text = text,
        responseType = response_type,
        responseOptions = response_options?.map {
            ResponseOption(it.id, it.label)
        },
        isCompulsory = is_compulsory
    )
}

