package com.example.healthassistant.data.remote.chat.mapper

import com.example.healthassistant.domain.model.chat.*
import com.example.healthassistant.data.remote.chat.dto.*
import com.example.healthassistant.core.utils.DateTime

fun ChatStartResponseDto.toDomain(): ChatMessage {
    return ChatMessage(
        id = session_id + "_start",
        sessionId = session_id,
        role = Role.ASSISTANT,
        content = message,
        timestamp = DateTime.getCurrentTimeMillis()
    )
}

fun ChatMessageResponseDto.toDomain(
    sessionId: String
): ChatMessage {
    return ChatMessage(
        id = sessionId + "_reply_" + DateTime.getCurrentTimeMillis(),
        sessionId = sessionId,
        role = Role.ASSISTANT,
        content = message,
        timestamp = DateTime.getCurrentTimeMillis()
    )
}
