package com.example.healthassistant.presentation.auth

sealed class AuthEvent {
    data class OnEmailChange(val email: String) : AuthEvent()
    data class OnPasswordChange(val password: String) : AuthEvent()
    data class OnConfirmPasswordChange(val confirmPassword: String) : AuthEvent()

    object OnLoginClick : AuthEvent()
    object OnSignupClick : AuthEvent()
}