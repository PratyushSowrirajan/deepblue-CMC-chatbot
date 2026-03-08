package com.example.healthassistant.presentation.assessment

import com.example.healthassistant.domain.model.assessment.Question
import com.example.healthassistant.domain.model.assessment.Report
import com.example.healthassistant.presentation.assessment.model.AnswerUiModel

data class AssessmentState(
    val isLoading: Boolean = true,
    val sessionId: String = "",
    val currentQuestion: Question? = null,
    val typedText: String = "",
    val recognizedSpeech: String = "",
    val isListening: Boolean = false,
    val isMuted: Boolean = false,
    val errorMessage: String? = null,
    val isCompleted: Boolean = false,
    val report: Report? = null,
    val isGeneratingReport: Boolean = false,

    // VISUAL MODE
    val isVisualModeActive: Boolean = false,
    val visualNavigationStack: List<String> = emptyList(), // body → subpart
    val isSubmitting: Boolean = false,

    val isImageQuestion: Boolean = false,
    val selectedImageBytes: ByteArray? = null,
    val selectedImageFileName: String? = null,


)

