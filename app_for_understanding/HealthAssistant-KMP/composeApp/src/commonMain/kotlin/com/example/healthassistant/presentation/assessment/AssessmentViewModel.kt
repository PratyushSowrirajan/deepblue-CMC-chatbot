package com.example.healthassistant.presentation.assessment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthassistant.domain.repository.AssessmentRepository
import com.example.healthassistant.core.stt.SpeechToTextManager
import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.core.tts.TextToSpeechManager
import com.example.healthassistant.data.remote.assessment.dto.AnswerDto
import com.example.healthassistant.domain.model.assessment.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AssessmentViewModel(
    private val repository: AssessmentRepository,
    private val speechToTextManager: SpeechToTextManager,
    private val ttsManager: TextToSpeechManager
) : ViewModel() {

    private val _state = MutableStateFlow(AssessmentState(isLoading = true))
    val state: StateFlow<AssessmentState> = _state

    val voiceAmplitude: Float = 0f  // 0f → 1f


    // ✅ START ASSESSMENT
    fun startAssessment() {


        AppLogger.d("VM", "startAssessment() called")

        viewModelScope.launch {

            AppLogger.d("VM", "Setting loading = true")

            _state.value = _state.value.copy(isLoading = true)

            try {
                AppLogger.d("VM", "Calling repository.startAssessment()")

                val session = repository.startAssessment()

                AppLogger.d(
                    "VM",
                    "START SUCCESS → sessionId=${session.sessionId}, question=${session.question.text}"
                )

                _state.value = _state.value.copy(
                    sessionId = session.sessionId
                )

                handleIncomingQuestion(session.question)



            } catch (e: Exception) {

                AppLogger.d("VM", "START ERROR → ${e.message}")

                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Unable to start assessment"
                )
            }
        }
    }


    // ✅ EVENTS
    fun onEvent(event: AssessmentEvent) {
        when (event) {

            is AssessmentEvent.SendImage -> {

                val question = _state.value.currentQuestion ?: return
                val imageBytes = _state.value.selectedImageBytes ?: return
                val fileName = _state.value.selectedImageFileName ?: "image.jpg"

                if (_state.value.isCompleted) return

                submitImageAnswer(question, imageBytes, fileName)
            }

            is AssessmentEvent.TextChanged -> {
                _state.value = _state.value.copy(
                    typedText = event.text
                )
            }


            is AssessmentEvent.OptionSelected -> {
                val question = _state.value.currentQuestion ?: return
                AppLogger.d("VM", "Option selected: ${event.optionId}")
                if (_state.value.isCompleted) return

                submitSingleChoiceAnswer(question, event.optionId)
            }

            is AssessmentEvent.SendText -> {
                val question = _state.value.currentQuestion ?: return
                val text = _state.value.typedText
                if (text.isNotBlank()) {
                    if (_state.value.isCompleted) return

                    submitTextAnswer(question, text)
                }
            }


            is AssessmentEvent.MicClicked -> {
                startListening()
            }

            is AssessmentEvent.StopListening -> {
                stopListening()
            }

            is AssessmentEvent.SpeechRecognized -> {
                _state.value = _state.value.copy(
                    recognizedSpeech = event.text,
                    typedText = event.text
                )
            }

            is AssessmentEvent.VolumeClicked -> {

                val newMuteState = !_state.value.isMuted

                if (newMuteState) {
                    ttsManager.stop()
                }

                _state.value = _state.value.copy(
                    isMuted = newMuteState
                )
            }

            is AssessmentEvent.OpenVisualMode -> {
                _state.value = _state.value.copy(isVisualModeActive = true)
            }

            is AssessmentEvent.CloseVisualMode -> {
                _state.value = _state.value.copy(
                    isVisualModeActive = false,
                    visualNavigationStack = emptyList()
                )
            }

            is AssessmentEvent.BodyPartSelected -> {
                _state.value = _state.value.copy(
                    visualNavigationStack =
                        _state.value.visualNavigationStack + event.partId
                )
            }

            AssessmentEvent.VisualBackPressed -> {
                val stack = _state.value.visualNavigationStack
                if (stack.isNotEmpty()) {
                    _state.value = _state.value.copy(
                        visualNavigationStack = stack.dropLast(1)
                    )
                } else {
                    _state.value = _state.value.copy(
                        isVisualModeActive = false
                    )
                }
            }

            is AssessmentEvent.VisualSymptomSelected -> {
                val question = _state.value.currentQuestion ?: return
                if (_state.value.isSubmitting) return

                _state.value = _state.value.copy(isSubmitting = true)

                viewModelScope.launch {

                    try {
                        val answerDto = AnswerDto(
                            type = "visual",
                            selected_option_id = event.symptomId,
                            selected_option_label = event.symptomLabel
                        )

                        val session = repository.submitAnswer(
                            question = question,
                            answer = answerDto,
                            imageBytes = _state.value.selectedImageBytes,
                            imageFileName = _state.value.selectedImageFileName
                        )

                        _state.value = _state.value.copy(
                            isVisualModeActive = false,
                            visualNavigationStack = emptyList(),
                            isSubmitting = false
                        )

                        if (session == null) {
                            generateReport()
                            return@launch
                        }

                        handleIncomingQuestion(session.question)

                    } catch (e: Exception) {
                        _state.value = _state.value.copy(
                            isSubmitting = false,
                            errorMessage = "Failed to submit visual answer"
                        )
                    }
                }
            }


            AssessmentEvent.ExitClicked -> {
                AppLogger.d("VM", "Exit clicked")
            }

            else -> {
                // Ignore old events like MyselfSelected, etc.
            }
        }
    }

    private fun submitTextAnswer(question: Question, text: String) {

        ttsManager.stop()

        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {
                val answerDto = AnswerDto(
                    type = question.responseType,
                    value = text
                )

                val session = repository.submitAnswer(
                    question = question,
                    answer = answerDto,
                    imageBytes = _state.value.selectedImageBytes,
                    imageFileName = _state.value.selectedImageFileName
                )

                // 🔥 IF backend says completed
                if (session == null) {

                    _state.value = _state.value.copy(
                        isGeneratingReport = true,
                        currentQuestion = null
                    )

                    generateReport()
                    return@launch
                }


                // Otherwise show next question
                handleIncomingQuestion(session.question)


            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to submit answer"
                )
            }
        }
    }


    private fun submitSingleChoiceAnswer(
        question: Question,
        selectedOptionId: String
    ) {
        ttsManager.stop()

        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {
                val selectedOption = question.responseOptions
                    ?.find { it.id == selectedOptionId }

                val answerDto = AnswerDto(
                    type = "single_choice",
                    selected_option_id = selectedOptionId,
                    selected_option_label = selectedOption?.label
                )

                val session = repository.submitAnswer(
                    question = question,
                    answer = answerDto,
                    imageBytes = _state.value.selectedImageBytes,
                    imageFileName = _state.value.selectedImageFileName
                )

                // 🔥 IF backend says completed
                if (session == null) {

                    _state.value = _state.value.copy(
                        isGeneratingReport = true,
                        currentQuestion = null
                    )

                    generateReport()
                    return@launch
                }


                handleIncomingQuestion(session.question)


            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to submit answer"
                )
            }
        }
    }


    private fun speakQuestionIfNeeded(question: Question) {

        if (_state.value.isMuted) return

        val textBuilder = StringBuilder()

        textBuilder.append(question.text)

        question.responseOptions?.let { options ->
            options.forEachIndexed { index, option ->
                textBuilder.append(". Option ${index + 1}. ${option.label}")
            }
        }

        ttsManager.speak(textBuilder.toString())
    }


    private fun generateReport() {
        ttsManager.stop()

        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {
                val report = repository.submitFinalReport()

                AppLogger.d("VM", "REPORT SUCCESS → id=${report.reportId}")

                _state.value = _state.value.copy(
                    isLoading = false,
                    isCompleted = true,
                    report = report,
                    currentQuestion = null
                )

            } catch (e: Exception) {

                AppLogger.d("VM", "REPORT ERROR → ${e.message}")

                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to generate report"
                )
            }
        }
    }






    // ✅ ERROR CLEAR
    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
        ttsManager.shutdown()
    }


    // ✅ SPEECH
    private fun startListening() {
        _state.value = _state.value.copy(isListening = true)

        speechToTextManager.startListening(
            onResult = { text ->
                onEvent(AssessmentEvent.SpeechRecognized(text))
            },
            onError = {
                _state.value = _state.value.copy(isListening = false)
            }
        )
    }

    private fun stopListening() {
        speechToTextManager.stopListening()
        _state.value = _state.value.copy(isListening = false)
    }

    private suspend fun handleIncomingQuestion(question: Question) {

        val stored = repository.getStoredAnswer(question.id)

        if (stored != null) {

            AppLogger.d("VM", "AUTO ANSWERING FROM SERVER → ${question.id}")

            val session = repository.submitAnswer(
                question = question,
                answer = stored,
                imageBytes = null,
                imageFileName = null
            )

            if (session == null) {
                generateReport()
                return
            }

            handleIncomingQuestion(session.question)
            return
        }

        // Show UI normally
        val isImage = question.responseType == "image"

        _state.value = _state.value.copy(
            isLoading = false,
            currentQuestion = question,
            typedText = "",
            errorMessage = null,
            isImageQuestion = isImage,
            selectedImageBytes = null,
            selectedImageFileName = null
        )

        speakQuestionIfNeeded(question)
    }

    fun endAssessment(onFinished: () -> Unit) {

        viewModelScope.launch {

            try {
                repository.endSession()
            } catch (e: Exception) {
                AppLogger.d("VM", "END SESSION ERROR → ${e.message}")
            }

            _state.value = AssessmentState() // Reset state

            onFinished()
        }
    }

    fun onImageSelected(bytes: ByteArray, fileName: String) {
        _state.value = _state.value.copy(
            selectedImageBytes = bytes,
            selectedImageFileName = fileName
        )
    }

    private fun submitImageAnswer(
        question: Question,
        imageBytes: ByteArray,
        fileName: String
    ) {

        ttsManager.stop()

        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {

                val answerDto = AnswerDto(
                    type = "image",
                    value = "image received"
                )

                val session = repository.submitAnswer(
                    question = question,
                    answer = answerDto,
                    imageBytes = imageBytes,
                    imageFileName = fileName
                )

                if (session == null) {

                    _state.value = _state.value.copy(
                        isGeneratingReport = true,
                        currentQuestion = null
                    )

                    generateReport()
                    return@launch
                }

                _state.value = _state.value.copy(
                    selectedImageBytes = null,
                    selectedImageFileName = null
                )

                handleIncomingQuestion(session.question)

            } catch (e: Exception) {

                AppLogger.d("IMAGE_UPLOAD", "Upload error: ${e.message}")
                e.printStackTrace()

                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to submit image"
                )
            }
        }
    }

}
