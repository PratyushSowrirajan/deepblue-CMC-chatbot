package com.example.healthassistant.presentation.auth

data class OnboardingMedicalState(
    val answers: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)