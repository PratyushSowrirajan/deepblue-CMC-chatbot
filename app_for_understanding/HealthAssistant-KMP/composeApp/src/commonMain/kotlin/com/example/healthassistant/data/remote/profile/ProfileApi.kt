package com.example.healthassistant.data.remote.profile

import com.example.healthassistant.data.remote.profile.dto.ProfileAnswerRequestDto
import com.example.healthassistant.data.remote.profile.dto.ProfileResponseDto

interface ProfileApi {

    suspend fun submitOnboardingProfile(
        request: ProfileAnswerRequestDto
    ): ProfileResponseDto

    suspend fun submitMedicalOnboarding(
        request: ProfileAnswerRequestDto
    ): ProfileResponseDto
}