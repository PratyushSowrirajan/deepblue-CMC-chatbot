package com.example.healthassistant.data.remote.assessment.dto

import kotlinx.serialization.Serializable

@Serializable
data class CauseDetailDto(
    val about_this: List<String>,
    val how_common: HowCommonDto,
    val what_you_can_do_now: List<String>,
    val warning: String? = null
)
