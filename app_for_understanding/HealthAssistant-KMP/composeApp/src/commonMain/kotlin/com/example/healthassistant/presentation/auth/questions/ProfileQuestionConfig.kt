package com.example.healthassistant.presentation.auth.questions

import com.example.healthassistant.presentation.auth.model.QuestionUiModel

object ProfileQuestionConfig {

    val questions = listOf(

        // -----------------------------
        // PERSONAL INFORMATION
        // -----------------------------

        QuestionUiModel(
            id = "q_name",
            questionText = "Full Name",
            type = "text",
            required = true
        ),

        QuestionUiModel(
            id = "q_age",
            questionText = "Age",
            type = "number",
            required = true
        ),

        QuestionUiModel(
            id = "q_gender",
            questionText = "Sex Assigned at Birth",
            type = "single_choice",
            options = listOf(
                "Male",
                "Female",
                "Intersex",
                "Prefer not to say"
            ),
            required = true
        ),

        QuestionUiModel(
            id = "q_city",
            questionText = "City of Residence",
            type = "text",
            required = true
        ),

        // -----------------------------
        // HEALTH INFORMATION
        // -----------------------------

        QuestionUiModel(
            id = "q_blood_group",
            questionText = "Blood Group",
            type = "single_choice",
            options = listOf(
                "A+",
                "A-",
                "B+",
                "B-",
                "AB+",
                "AB-",
                "O+",
                "O-",
                "Unknown"
            ),
            required = true
        ),

        // -----------------------------
        // PHYSICAL INFORMATION
        // -----------------------------

        QuestionUiModel(
            id = "q_height_range",
            questionText = "Height Range",
            type = "single_choice",
            options = listOf(
                "Below 140 cm",
                "140 – 150 cm",
                "150 – 160 cm",
                "160 – 170 cm",
                "170 – 180 cm",
                "180 – 190 cm",
                "Above 190 cm"
            ),
            required = false
        ),

        QuestionUiModel(
            id = "q_weight_range",
            questionText = "Weight Range",
            type = "single_choice",
            options = listOf(
                "Below 40 kg",
                "40 – 50 kg",
                "50 – 60 kg",
                "60 – 70 kg",
                "70 – 80 kg",
                "80 – 90 kg",
                "Above 90 kg"
            ),
            required = false
        ),

        // -----------------------------
        // LIFESTYLE INFORMATION
        // -----------------------------

        QuestionUiModel(
            id = "q_occupation",
            questionText = "Occupation",
            type = "single_choice",
            options = listOf(
                "Student",
                "Working Professional",
                "Self-Employed",
                "Homemaker",
                "Retired",
                "Unemployed",
                "Other"
            ),
            required = true
        )
    )

    // -----------------------------
    // FEMALE SPECIFIC QUESTIONS
    // -----------------------------

    val femaleConditional = listOf(

        QuestionUiModel(
            id = "q_pregnancy_status",
            questionText = "Pregnancy Status",
            type = "single_choice",
            options = listOf(
                "Pregnant",
                "Trying to conceive",
                "Postpartum",
                "Breastfeeding",
                "Menopausal",
                "Not applicable"
            ),
            required = false
        ),

        QuestionUiModel(
            id = "q_menstrual_status",
            questionText = "Menstrual Cycle Status",
            type = "single_choice",
            options = listOf(
                "Regular cycle",
                "Irregular cycle",
                "Currently menstruating",
                "Missed period",
                "Not applicable"
            ),
            required = false
        )
    )
}