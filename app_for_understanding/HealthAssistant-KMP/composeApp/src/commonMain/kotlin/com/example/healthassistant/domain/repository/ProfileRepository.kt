package com.example.healthassistant.domain.repository

import com.example.healthassistant.data.remote.profile.dto.ProfileAnswerRequestDto
import com.example.healthassistant.data.remote.profile.dto.ProfileResponseDto

interface ProfileRepository {

    suspend fun submitProfile(
        request: ProfileAnswerRequestDto
    ): ProfileResponseDto

    suspend fun submitMedical(
        request: ProfileAnswerRequestDto
    ): ProfileResponseDto
}