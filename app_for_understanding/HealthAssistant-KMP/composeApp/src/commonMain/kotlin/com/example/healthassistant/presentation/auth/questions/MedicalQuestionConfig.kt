package com.example.healthassistant.presentation.auth.questions

import com.example.healthassistant.presentation.auth.model.QuestionUiModel

object MedicalQuestionConfig {

    val questions = listOf(

        // -------------------------
        // MEDICAL HISTORY
        // -------------------------

        QuestionUiModel(
            id = "q_past_conditions",
            questionText = "Do you have any diagnosed medical conditions?",
            type = "single_choice",
            options = listOf(
                "yes",
                "no"
            )
        ),

        QuestionUiModel(
            id = "q_condition_details",
            questionText = "Please list your diagnosed medical conditions",
            type = "text",
            required = false
        ),

        // -------------------------
        // SURGICAL HISTORY
        // -------------------------

        QuestionUiModel(
            id = "q_surgeries",
            questionText = "Have you undergone any surgical procedures?",
            type = "single_choice",
            options = listOf(
                "yes",
                "no"
            )
        ),

        QuestionUiModel(
            id = "q_surgery_details",
            questionText = "Please describe the surgery and approximate year",
            type = "text",
            required = false
        ),

        // -------------------------
        // FAMILY HISTORY
        // -------------------------

        QuestionUiModel(
            id = "q_family_history",
            questionText = "Is there any significant medical history in your family?",
            type = "single_choice",
            options = listOf(
                "heart_disease",
                "diabetes",
                "hypertension",
                "cancer",
                "stroke",
                "none_known"
            )
        ),

        // -------------------------
        // MEDICATIONS
        // -------------------------

        QuestionUiModel(
            id = "q_current_medication",
            questionText = "Are you currently taking any medications or supplements?",
            type = "single_choice",
            options = listOf(
                "yes",
                "no"
            )
        ),

        QuestionUiModel(
            id = "q_medication_details",
            questionText = "Please list the medications or supplements you take regularly",
            type = "text",
            required = false
        ),

        // -------------------------
        // ALLERGIES
        // -------------------------

        QuestionUiModel(
            id = "q_allergies",
            questionText = "Do you have any known allergies?",
            type = "single_choice",
            options = listOf(
                "yes",
                "no"
            )
        ),

        QuestionUiModel(
            id = "q_allergy_details",
            questionText = "Please describe your allergies and reactions",
            type = "text",
            required = false
        ),

        // -------------------------
        // LIFESTYLE
        // -------------------------

        QuestionUiModel(
            id = "q_smoking",
            questionText = "What best describes your smoking status?",
            type = "single_choice",
            options = listOf(
                "never_smoked",
                "former_smoker",
                "current_smoker"
            )
        ),

        QuestionUiModel(
            id = "q_alcohol",
            questionText = "How often do you consume alcoholic beverages?",
            type = "single_choice",
            options = listOf(
                "never",
                "occasionally",
                "socially",
                "regularly"
            )
        )
    )
}