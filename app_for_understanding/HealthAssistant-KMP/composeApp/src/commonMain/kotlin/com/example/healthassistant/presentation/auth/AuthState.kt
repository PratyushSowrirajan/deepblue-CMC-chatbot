package com.example.healthassistant.presentation.auth

data class AuthState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val token: String? = null,      // ✅ REQUIRED
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)