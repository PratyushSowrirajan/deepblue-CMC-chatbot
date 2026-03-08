package com.example.healthassistant.data.repository

import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.data.remote.auth.AuthApi
import com.example.healthassistant.data.remote.auth.dto.*
import com.example.healthassistant.data.remote.profile.ProfileApi
import com.example.healthassistant.data.remote.profile.dto.ProfileAnswerRequestDto
import com.example.healthassistant.data.remote.profile.dto.ProfileResponseDto
import com.example.healthassistant.domain.repository.AuthRepository
import com.example.healthassistant.domain.repository.ProfileRepository


class ProfileRepositoryImpl(
    private val api: ProfileApi
) : ProfileRepository {

    override suspend fun submitProfile(
        request: ProfileAnswerRequestDto
    ): ProfileResponseDto {
        return api.submitOnboardingProfile(request)
    }

    override suspend fun submitMedical(
        request: ProfileAnswerRequestDto
    ): ProfileResponseDto {
        return api.submitMedicalOnboarding(request)
    }
}