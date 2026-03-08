package com.example.healthassistant.data.remote.chat.dto

import ChatReportWrapperDto
import kotlinx.serialization.Serializable

@Serializable
data class ChatStartRequestDto(
    val main_report_id: String? = null,
    val entry_point: String
)