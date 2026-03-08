package com.example.healthassistant.data.repository

import ChatReportWrapperDto
import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.core.utils.DateTime
import com.example.healthassistant.data.local.chat.ChatLocalDataSource
import com.example.healthassistant.data.local.profile.ProfileLocalDataSource
import com.example.healthassistant.data.local.report.ReportLocalDataSource
import com.example.healthassistant.data.remote.assessment.dto.ReportResponseDto
import com.example.healthassistant.data.remote.chat.ChatApi
import com.example.healthassistant.data.remote.chat.dto.ChatHistoryDto
import com.example.healthassistant.data.remote.chat.dto.ChatMessageRequestDto
import com.example.healthassistant.data.remote.chat.dto.ChatStartRequestDto
import com.example.healthassistant.data.remote.chat.dto.ProfileDataDto
import com.example.healthassistant.data.remote.chat.dto.ProfileItemDto
import com.example.healthassistant.data.remote.chat.mapper.toDomain
import com.example.healthassistant.data.remote.chat.mapper.toReportResponseDto
import com.example.healthassistant.domain.model.chat.ChatMessage
import com.example.healthassistant.domain.model.chat.Role
import com.example.healthassistant.domain.repository.ChatRepository
import kotlinx.serialization.json.Json

class ChatRepositoryImpl(
    private val api: ChatApi,
    private val local: ChatLocalDataSource,
) : ChatRepository {

    override suspend fun startChat(
        currentReportId: String?
    ): ChatMessage {

        val entryPoint =
            if (currentReportId != null) "assessment"
            else "home"

        val request = ChatStartRequestDto(
            main_report_id = currentReportId,
            entry_point = entryPoint
        )

        val response = api.startChat(request)
        AppLogger.logJson("CHAT_REPO", "START RESPONSE", response)

        val assistantMessage = response.toDomain()

        local.insert(assistantMessage)

        return assistantMessage
    }

    override suspend fun sendMessage(
        sessionId: String,
        userMessage: String
    ): ChatMessage {

        AppLogger.d("CHAT_REPO", "===== SEND MESSAGE BEGIN =====")
        AppLogger.d("CHAT_REPO", "Session ID → $sessionId")
        AppLogger.d("CHAT_REPO", "User message → $userMessage")

        val user = ChatMessage(
            id = sessionId + "_user_" + DateTime.getCurrentTimeMillis(),
            sessionId = sessionId,
            role = Role.USER,
            content = userMessage,
            timestamp = DateTime.getCurrentTimeMillis()
        )

        local.insert(user)

        val request = ChatMessageRequestDto(
            session_id = sessionId,
            message = userMessage
        )
        AppLogger.logJson("CHAT_REPO", "MESSAGE REQUEST BODY", request)

        val response = api.sendMessage(request)
        AppLogger.d("CHAT_REPO", "Assistant reply received")
        AppLogger.d("CHAT_REPO", "Reply content → ${response.message}")

        val assistantMessage = response.toDomain(sessionId)

        local.insert(assistantMessage)
        AppLogger.d("CHAT_REPO", "===== SEND MESSAGE END =====")

        return assistantMessage
    }

    override suspend fun getMessages(sessionId: String): List<ChatMessage> {
        return local.getMessages(sessionId)
    }

    override suspend fun endChat(sessionId: String) {
        try {
            api.endChat(sessionId)
        } catch (_: Exception) {
        }

        local.clear(sessionId)
    }
}