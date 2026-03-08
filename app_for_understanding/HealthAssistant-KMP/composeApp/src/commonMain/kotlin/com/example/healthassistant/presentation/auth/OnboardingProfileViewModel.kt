package com.example.healthassistant.presentation.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.data.remote.profile.dto.ProfileAnswerDto
import com.example.healthassistant.data.remote.profile.dto.ProfileAnswerItemDto
import com.example.healthassistant.data.remote.profile.dto.ProfileAnswerRequestDto
import com.example.healthassistant.domain.repository.ProfileRepository
import com.example.healthassistant.presentation.auth.model.EmergencyContact
import com.example.healthassistant.presentation.auth.questions.ProfileQuestionConfig
import kotlinx.coroutines.launch

class OnboardingProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    var state = mutableStateOf(OnboardingProfileState())
        private set

    fun onDynamicValueChange(id: String, value: String) {
        state.value = state.value.copy(
            answers = state.value.answers + (id to value)
        )
    }

    fun getValueForQuestion(id: String): String {
        return state.value.answers[id] ?: ""
    }

    fun submitProfile() {

        val answers = state.value.answers
        AppLogger.d(
            "PROFILE_IMAGE",
            "Submitting profile → image present: ${state.value.profileImageBase64 != null}"
        )

        // -----------------------------
        // REQUIRED QUESTIONS
        // -----------------------------

        val requiredQuestions = listOf(
            "q_name",
            "q_age",
            "q_gender",
            "q_city",
            "q_blood_group",
            "q_occupation"
        )

        for (questionId in requiredQuestions) {
            if (answers[questionId].isNullOrBlank()) {

                state.value = state.value.copy(
                    errorMessage = "Please complete all required fields"
                )
                return
            }
        }

        // -----------------------------
        // PROFILE IMAGE VALIDATION
        // -----------------------------

        if (state.value.profileImageBase64 == null) {

            state.value = state.value.copy(
                errorMessage = "Please upload a profile image"
            )
            return
        }

        // -----------------------------
        // EMERGENCY CONTACT VALIDATION
        // -----------------------------

        val validContacts = state.value.emergencyContacts.filter {
            it.name.isNotBlank() && it.number.isNotBlank()
        }

        if (validContacts.isEmpty()) {

            state.value = state.value.copy(
                errorMessage = "Please add at least one emergency contact"
            )
            return
        }

        // -----------------------------
        // PROCEED TO API CALL
        // -----------------------------

        viewModelScope.launch {

            state.value = state.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {

                val request = buildRequest()
                AppLogger.logJson("PROFILE_API", "PROFILE REQUEST", request)

                val response = repository.submitProfile(request)
                AppLogger.logJson("PROFILE_API", "PROFILE RESPONSE", response)

                if (response.success) {

                    state.value = state.value.copy(
                        isSuccess = true
                    )

                } else {

                    state.value = state.value.copy(
                        errorMessage = response.message
                    )
                }

            } catch (e: Exception) {

                state.value = state.value.copy(
                    errorMessage = e.message ?: "Something went wrong"
                )
            }

            state.value = state.value.copy(isLoading = false)
        }
    }

    private fun buildRequest(): ProfileAnswerRequestDto {

        val allQuestions =
            ProfileQuestionConfig.questions +
                    ProfileQuestionConfig.femaleConditional

        val answerList = mutableListOf<ProfileAnswerItemDto>()

        // -------------------------------------------------
        // 1️⃣ PROFILE IMAGE
        // -------------------------------------------------

        state.value.profileImageBase64?.let { base64 ->
            AppLogger.d("PROFILE_IMAGE", "Adding image to request")
            AppLogger.d("PROFILE_IMAGE", "Base64 size: ${base64.length}")

            answerList.add(
                ProfileAnswerItemDto(
                    question_id = "q_profile_image",
                    question_text = "Profile Image",
                    answer_json = ProfileAnswerDto(
                        type = "image",
                        value = base64
                    )
                )
            )
        }

        // -------------------------------------------------
        // 2️⃣ NORMAL QUESTIONS
        // -------------------------------------------------

        state.value.answers.forEach { (id, value) ->

            val question = allQuestions.firstOrNull { it.id == id }
                ?: return@forEach

            val dto = when (question.type) {

                "text" -> ProfileAnswerDto(
                    type = "text",
                    value = value
                )

                "single_choice" -> ProfileAnswerDto(
                    type = "single_choice",
                    selected_option_label = value
                )

                else -> ProfileAnswerDto(
                    type = question.type,
                    value = value
                )
            }

            answerList.add(
                ProfileAnswerItemDto(
                    question_id = id,
                    question_text = question.questionText,
                    answer_json = dto
                )
            )
        }

        // -------------------------------------------------
        // 3️⃣ AGE (NUMBER INPUT)
        // -------------------------------------------------

        state.value.answers["q_age"]?.toIntOrNull()?.let { age ->

            answerList.removeAll { it.question_id == "q_age" }

            answerList.add(
                ProfileAnswerItemDto(
                    question_id = "q_age",
                    question_text = "Age",
                    answer_json = ProfileAnswerDto(
                        type = "number",
                        number_value = age
                    )
                )
            )
        }

        // -------------------------------------------------
        // 4️⃣ EMERGENCY CONTACTS LIST
        // -------------------------------------------------

        val contacts = state.value.emergencyContacts
            .filter { it.name.isNotBlank() && it.number.isNotBlank() }

        if (contacts.isNotEmpty()) {

            val jsonContacts = contacts.joinToString(
                prefix = "[",
                postfix = "]"
            ) {
                """{"name":"${it.name}","number":"${it.number}"}"""
            }

            answerList.add(
                ProfileAnswerItemDto(
                    question_id = "q_emergency_contacts",
                    question_text = "Emergency Contacts",
                    answer_json = ProfileAnswerDto(
                        type = "list",
                        value = jsonContacts
                    )
                )
            )
        }

        return ProfileAnswerRequestDto(
            answer_json = answerList
        )
    }

    fun resetSuccess() {
        state.value = state.value.copy(isSuccess = false)
    }

    fun updateProfileImage(base64: String) {
        AppLogger.d("PROFILE_IMAGE", "Image received in ViewModel")
        AppLogger.d("PROFILE_IMAGE", "Base64 length: ${base64.length}")

        state.value = state.value.copy(profileImageBase64 = base64)
    }

    fun updateContactName(index: Int, value: String) {

        val list = state.value.emergencyContacts.toMutableList()
        list[index] = list[index].copy(name = value)

        state.value = state.value.copy(
            emergencyContacts = list
        )
    }

    fun updateContactNumber(index: Int, value: String) {

        val list = state.value.emergencyContacts.toMutableList()
        list[index] = list[index].copy(number = value)

        state.value = state.value.copy(
            emergencyContacts = list
        )
    }

    fun addEmergencyContact() {

        val list = state.value.emergencyContacts.toMutableList()
        list.add(EmergencyContact())

        state.value = state.value.copy(
            emergencyContacts = list
        )
    }
}