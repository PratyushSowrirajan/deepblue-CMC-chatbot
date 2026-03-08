package com.example.healthassistant.data.remote.chat

import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.data.remote.chat.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class ChatApiImpl(
    private val client: HttpClient,
    private val baseUrl: String
) : ChatApi {

    override suspend fun startChat(
        request: ChatStartRequestDto
    ): ChatStartResponseDto {

        AppLogger.d("CHAT_API", "POST /chat/start")

        val response = client.post("$baseUrl/chat/start") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        val body = response.body<ChatStartResponseDto>()

        AppLogger.logJson("CHAT_API", "RESPONSE BODY", body)

        return body
    }




    override suspend fun sendMessage(
        request: ChatMessageRequestDto
    ): ChatMessageResponseDto {

        AppLogger.d("CHAT_API", "POST /chat/message")

        val response = client.post("$baseUrl/chat/message") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        val body = response.body<ChatMessageResponseDto>()

        AppLogger.logJson("CHAT_API", "REQUEST BODY", request)
        AppLogger.logJson("CHAT_API", "RESPONSE BODY", body)

        return body
    }

    override suspend fun endChat(sessionId: String) {
        AppLogger.d("CHAT_API", "POST /chat/message")


        client.post("$baseUrl/chat/end") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("session_id" to sessionId))
        }
    }
}
