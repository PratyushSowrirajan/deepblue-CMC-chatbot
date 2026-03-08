package com.example.healthassistant.data.local.chat

import com.example.healthassistant.db.HealthDatabase
import com.example.healthassistant.domain.model.chat.ChatMessage
import com.example.healthassistant.domain.model.chat.Role

class ChatLocalDataSourceImpl(
    private val database: HealthDatabase
) : ChatLocalDataSource {

    override suspend fun insert(message: ChatMessage) {
        database.chatMessagesQueries.insertMessage(
            id = message.id,
            session_id = message.sessionId,
            role = message.role.name,
            content = message.content,
            timestamp = message.timestamp
        )
    }

    override suspend fun getMessages(sessionId: String): List<ChatMessage> {
        return database.chatMessagesQueries
            .getMessagesBySession(sessionId)
            .executeAsList()
            .map {
                ChatMessage(
                    id = it.id,
                    sessionId = it.session_id,
                    role = Role.valueOf(it.role),
                    content = it.content,
                    timestamp = it.timestamp
                )
            }
    }

    override suspend fun clear(sessionId: String) {
        database.chatMessagesQueries.deleteBySession(sessionId)
    }
}
