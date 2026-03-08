package com.example.healthassistant.presentation.home

sealed class HomeEvent {

    object StartAssessment : HomeEvent()

    data class SuggestionClicked(
        val text: String
    ) : HomeEvent()

    data class QuickHelpClicked(
        val item: QuickHelpItem
    ) : HomeEvent()

    object SettingsClicked : HomeEvent()

    object OpenChat : HomeEvent()
}
