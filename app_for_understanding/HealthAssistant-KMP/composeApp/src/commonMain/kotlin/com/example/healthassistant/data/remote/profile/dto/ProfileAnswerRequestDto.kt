package com.example.healthassistant.data.remote.profile.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileAnswerRequestDto(
    val answer_json: List<ProfileAnswerItemDto>
)