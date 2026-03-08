package com.example.healthassistant

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.healthassistant.designsystem.HealthAssistantTheme
import com.example.healthassistant.presentation.assessment.AssessmentScreen
import com.example.healthassistant.presentation.assessment.AssessmentViewModel
import com.example.healthassistant.data.remote.assessment.AssessmentApiImpl
import com.example.healthassistant.data.repository.AssessmentRepositoryImpl
import com.example.healthassistant.core.network.createHttpClient
import com.example.healthassistant.stt.DesktopSpeechToTextManager

@Composable
fun DesktopApp() {
    HealthAssistantTheme {

        val api = remember {
            AssessmentApiImpl(
                client = createHttpClient(),
                baseUrl = "http://localhost:8000"
            )
        }

        val repository = remember {
            AssessmentRepositoryImpl(api)
        }

        val speechToTextManager = remember {
            DesktopSpeechToTextManager(
                modelPath = "models/vosk-en" // adjust path
            )
        }

        val viewModel = remember {
            AssessmentViewModel(
                repository = repository,
                speechToTextManager = speechToTextManager
            )
        }

        AssessmentScreen(
            viewModel = viewModel,
            onExit = { /* optional */ }
        )
    }
}
