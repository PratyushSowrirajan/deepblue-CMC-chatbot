package com.example.healthassistant.presentation.auth

sealed class OnboardingProfileEvent {

    data class OnFullNameChange(val value: String) : OnboardingProfileEvent()
    data class OnAgeChange(val value: String) : OnboardingProfileEvent()
    data class OnGenderChange(val value: String) : OnboardingProfileEvent()
    data class OnEmergencyNameChange(val value: String) : OnboardingProfileEvent()
    data class OnEmergencyNumberChange(val value: String) : OnboardingProfileEvent()
    data class OnBloodGroupChange(val value: String) : OnboardingProfileEvent()
    data class OnHeightChange(val value: String) : OnboardingProfileEvent()
    data class OnWeightChange(val value: String) : OnboardingProfileEvent()
    data class OnCityChange(val value: String) : OnboardingProfileEvent()
    data class OnOccupationChange(val value: String) : OnboardingProfileEvent()

    object OnContinueClick : OnboardingProfileEvent()
}