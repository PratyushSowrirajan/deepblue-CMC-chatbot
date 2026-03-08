//package com.example.healthassistant.data.remote.assessment.dto
//
//import com.example.healthassistant.presentation.assessment.model.AnswerUiModel
//import com.example.healthassistant.presentation.assessment.model.QuestionUiModel
//
//fun QuestionDto.toUiModel(): QuestionUiModel {
//    return QuestionUiModel(
//        id = questionId,
//        title = title,
//        step = step,
//        totalSteps = total_steps,
//        question = question,
//        options = options.map {
//            AnswerUiModel(
//                id = it.id,
//                label = it.label
//            )
//        }
//    )
//}
