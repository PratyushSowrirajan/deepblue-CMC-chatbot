package com.example.healthassistant.core.auth

object TokenManager {

    private var token: String? = null

    fun saveToken(newToken: String) {
        token = newToken
    }

    fun getToken(): String? = token

    fun clearToken() {
        token = null
    }
}