package com.example.healthassistant.presentation.oldhome

sealed class HomeEvent {
    object StartAssessment : HomeEvent()
    data class SuggestionClicked(val text: String) : HomeEvent()
    object EmergencyClicked : HomeEvent()
}
