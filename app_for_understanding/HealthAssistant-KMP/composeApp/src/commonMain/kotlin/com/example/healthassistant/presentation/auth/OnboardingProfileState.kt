package com.example.healthassistant.presentation.auth

import com.example.healthassistant.presentation.auth.model.EmergencyContact

data class OnboardingProfileState(

    val answers: Map<String, String> = emptyMap(),

    val profileImageBase64: String? = null,

    val emergencyContacts: List<EmergencyContact> =
        listOf(EmergencyContact()),

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)