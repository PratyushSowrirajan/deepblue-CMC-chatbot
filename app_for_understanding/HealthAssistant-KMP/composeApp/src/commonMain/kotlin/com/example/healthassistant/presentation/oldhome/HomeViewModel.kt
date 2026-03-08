package com.example.healthassistant.presentation.oldhome

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(
    private val onStartAssessment: () -> Unit
) : ViewModel() {


    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.StartAssessment -> {
                onStartAssessment()
                // navigation later
            }
            is HomeEvent.SuggestionClicked -> {
                // start assessment with this text later
            }
            HomeEvent.EmergencyClicked -> {
                // open emergency flow later
            }
        }
    }


}
