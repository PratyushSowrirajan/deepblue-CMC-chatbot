package com.example.healthassistant.presentation.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(
    private val onStartAssessment: () -> Unit,
    private val onOpenChat: () -> Unit,
    private val onOpenSettings: () -> Unit
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    fun onEvent(event: HomeEvent) {
        when (event) {

            HomeEvent.OpenChat -> {
                onOpenChat()
            }
            HomeEvent.SettingsClicked -> {
                onOpenSettings()
            }

            HomeEvent.StartAssessment -> {
                onStartAssessment()
            }

            is HomeEvent.SuggestionClicked -> {
                // handle later
            }

            is HomeEvent.QuickHelpClicked -> {
                // handle later
            }

            HomeEvent.SettingsClicked -> {
                // handle later
            }
        }
    }
}
