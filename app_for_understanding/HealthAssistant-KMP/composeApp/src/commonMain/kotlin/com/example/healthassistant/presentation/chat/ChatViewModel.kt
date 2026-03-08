package com.example.healthassistant.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.core.stt.SpeechToTextManager
import com.example.healthassistant.core.tts.TextToSpeechManager
import com.example.healthassistant.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository,
    private val speechToTextManager: SpeechToTextManager,
    private val ttsManager: TextToSpeechManager
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state

    // 🔥 Start Chat
    fun startChat(currentReportId: String? = null) {

        viewModelScope.launch {

            _state.update { it.copy(isLoading = true) }

            try {
                val firstMessage = repository.startChat(currentReportId)

                _state.update {
                    it.copy(
                        isLoading = false,
                        sessionId = firstMessage.sessionId,
                        messages = listOf(firstMessage)
                    )
                }

                // 🔊 Auto speak first message
                if (_state.value.isTtsEnabled) {
                    ttsManager.speak(firstMessage.content)
                }

            } catch (e: Exception) {

                AppLogger.d("CHAT_VM", "START CHAT ERROR → ${e.message}")
                e.printStackTrace()

                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to start chat"
                    )
                }
            }
        }
    }

    // 🔥 Handle UI Events
    fun onEvent(event: ChatEvent) {
        when (event) {

            is ChatEvent.MessageChanged -> {
                _state.update { it.copy(typedMessage = event.text) }
            }

            ChatEvent.SendMessage -> sendMessage()

            ChatEvent.Retry -> startChat()

            ChatEvent.ToggleMic -> toggleMic()

            ChatEvent.ToggleTts -> toggleTts()
        }
    }

    // 🔥 Send Message
    private fun sendMessage() {

        val sessionId = _state.value.sessionId ?: return
        val message = _state.value.typedMessage.trim()

        if (message.isEmpty()) return

        // 🛑 Stop mic if active
        if (speechToTextManager.isListening()) {
            speechToTextManager.stopListening()
            _state.update { it.copy(isListening = false) }
        }

        viewModelScope.launch {

            _state.update {
                it.copy(
                    isLoading = true,
                    typedMessage = ""
                )
            }

            try {
                val assistantReply =
                    repository.sendMessage(sessionId, message)

                val updatedHistory =
                    repository.getMessages(sessionId)

                _state.update {
                    it.copy(
                        isLoading = false,
                        messages = updatedHistory
                    )
                }

                // 🔊 Speak assistant reply
                if (_state.value.isTtsEnabled) {
                    ttsManager.speak(assistantReply.content)
                }

            } catch (e: Exception) {

                _state.update {
                    it.copy(isLoading = false, error = "Failed to send message")
                }
            }
        }
    }

    fun endChat() {

        val sessionId = _state.value.sessionId ?: return

        viewModelScope.launch {
            try {
                repository.endChat(sessionId)
            } catch (_: Exception) {}

            // 🛑 Stop everything
            speechToTextManager.stopListening()
            ttsManager.stop()

            _state.value = ChatState()
        }
    }

    // 🎤 MIC TOGGLE
    private fun toggleMic() {

        val listening = speechToTextManager.isListening()

        if (listening) {
            speechToTextManager.stopListening()
            _state.update { it.copy(isListening = false) }
        } else {

            // Optional: stop TTS while listening
            ttsManager.stop()

            speechToTextManager.startListening(
                onResult = { result ->
                    _state.update { it.copy(typedMessage = result) }
                },
                onError = {
                    _state.update { it.copy(isListening = false) }
                }
            )

            _state.update { it.copy(isListening = true) }
        }
    }

    // 🔊 TTS TOGGLE
    private fun toggleTts() {

        val enabled = !_state.value.isTtsEnabled

        if (!enabled) {
            ttsManager.stop()
        }

        _state.update { it.copy(isTtsEnabled = enabled) }
    }
}