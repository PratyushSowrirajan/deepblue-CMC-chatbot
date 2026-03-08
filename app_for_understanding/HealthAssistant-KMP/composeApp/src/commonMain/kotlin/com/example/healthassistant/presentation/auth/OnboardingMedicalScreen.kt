package com.example.healthassistant.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.healthassistant.presentation.auth.components.QuestionInput
import com.example.healthassistant.presentation.auth.questions.MedicalQuestionConfig
import com.example.healthassistant.presentation.components.ErrorMessageCard
import com.example.healthassistant.presentation.components.OnboardingProgress

@Composable
fun OnboardingMedicalScreen(
    viewModel: OnboardingMedicalViewModel,
    onMedicalCompleted: () -> Unit
) {

    val state = viewModel.state.value
    val answers = state.answers


    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onMedicalCompleted()
            viewModel.resetSuccess()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        item {

            state.errorMessage?.let { error ->

                ErrorMessageCard(error)

                Spacer(Modifier.height(16.dp))
            }
        }

        item {
            OnboardingProgress(
                currentStep = 2,
                totalSteps = 2,
                title = "Medical Information"
            )

            Spacer(Modifier.height(16.dp))
        }

        items(MedicalQuestionConfig.questions) { baseQuestion ->

            val currentValue = viewModel.getValueForQuestion(baseQuestion.id)
            val showErrors = state.errorMessage != null

            // CONDITIONAL QUESTIONS

            if (
                baseQuestion.id == "q_condition_details" &&
                answers["q_past_conditions"] != "yes"
            ) return@items

            if (
                baseQuestion.id == "q_surgery_details" &&
                answers["q_surgeries"] != "yes"
            ) return@items

            if (
                baseQuestion.id == "q_medication_details" &&
                answers["q_current_medication"] != "yes"
            ) return@items

            if (
                baseQuestion.id == "q_allergy_details" &&
                answers["q_allergies"] != "yes"
            ) return@items

            QuestionInput(
                question = baseQuestion.copy(value = currentValue),
                onValueChange = {
                    viewModel.onDynamicValueChange(baseQuestion.id, it)
                },
                isRequiredError =
                    showErrors &&
                            baseQuestion.required &&
                            currentValue.isBlank()
            )
        }

        item {

            Button(
                onClick = { viewModel.submitMedical() },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {

                if (state.isLoading) {

                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )

                } else {

                    Text("Continue")
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}