package com.example.healthassistant.presentation.navigation

import com.example.healthassistant.domain.model.assessment.PossibleCause


sealed class AppScreen {

    object Login : AppScreen()
    object Signup : AppScreen()

    object OnboardingProfile : AppScreen()
    object OnboardingMedical : AppScreen()

    object Home : AppScreen()
    object Settings : AppScreen()
    object Assessment : AppScreen()

    object AssessmentReport : AppScreen()

    data class AssessmentCauseDetail(
        val cause: PossibleCause
    ) : AppScreen()

    object History : AppScreen()
    object News : AppScreen()

//    object AssessmentStart : AppScreen()

    object HistoryDetail : AppScreen()
    data class CauseDetail(val title: String) : AppScreen()

    data class Chat(val reportId: String?) : AppScreen()
}
