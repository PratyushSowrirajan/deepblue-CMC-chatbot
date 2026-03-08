package com.example.healthassistant.data.remote.profile.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileAnswerItemDto(
    val question_id: String,
    val question_text: String,
    val answer_json: ProfileAnswerDto
)