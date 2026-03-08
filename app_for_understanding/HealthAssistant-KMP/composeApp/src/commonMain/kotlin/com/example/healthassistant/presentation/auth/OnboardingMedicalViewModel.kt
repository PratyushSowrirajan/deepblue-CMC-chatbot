package com.example.healthassistant.presentation.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthassistant.data.remote.profile.dto.ProfileAnswerDto
import com.example.healthassistant.data.remote.profile.dto.ProfileAnswerItemDto
import com.example.healthassistant.data.remote.profile.dto.ProfileAnswerRequestDto
import com.example.healthassistant.domain.repository.ProfileRepository
import com.example.healthassistant.presentation.auth.questions.MedicalQuestionConfig
import kotlinx.coroutines.launch


class OnboardingMedicalViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    var state = mutableStateOf(OnboardingMedicalState())
        private set

    fun submitMedical() {

        val requiredQuestions = listOf(
            "q_past_conditions",
            "q_surgeries",
            "q_family_history",
            "q_current_medication",
            "q_allergies",
            "q_smoking",
            "q_alcohol"
        )

        for (questionId in requiredQuestions) {

            if (state.value.answers[questionId].isNullOrBlank()) {

                state.value = state.value.copy(
                    errorMessage = "Please complete all required fields"
                )

                return
            }
        }

        viewModelScope.launch {

            state.value = state.value.copy(isLoading = true)

            try {

                val request = buildRequest()

                val response = repository.submitMedical(request)

                if (response.success) {
                    state.value = state.value.copy(isSuccess = true)
                } else {
                    state.value = state.value.copy(
                        errorMessage = response.message
                    )
                }

            } catch (e: Exception) {
                state.value = state.value.copy(
                    errorMessage = e.message
                )
            }

            state.value = state.value.copy(isLoading = false)
        }
    }

    private fun buildRequest(): ProfileAnswerRequestDto {

        val answerList = state.value.answers.map { (id, value) ->

            val questionConfig =
                MedicalQuestionConfig.questions.first { it.id == id }

            ProfileAnswerItemDto(
                question_id = id,
                question_text = questionConfig.questionText,
                answer_json = ProfileAnswerDto(
                    type = questionConfig.type,
                    value = if (questionConfig.type == "text") value else null,
                    selected_option_label =
                        if (questionConfig.type == "single_choice") value else null
                )
            )
        }

        return ProfileAnswerRequestDto(answer_json = answerList)
    }

    fun resetSuccess() {
        state.value = state.value.copy(isSuccess = false)
    }

    fun onDynamicValueChange(id: String, value: String) {
        state.value = state.value.copy(
            answers = state.value.answers + (id to value)
        )
    }

    fun getValueForQuestion(id: String): String {
        return state.value.answers[id] ?: ""
    }
}