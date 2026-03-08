package com.example.healthassistant.presentation.chat

sealed class ChatEvent {

    data class MessageChanged(val text: String) : ChatEvent()

    object SendMessage : ChatEvent()

    object Retry : ChatEvent()

    object ToggleMic : ChatEvent()
    object ToggleTts : ChatEvent()
}
