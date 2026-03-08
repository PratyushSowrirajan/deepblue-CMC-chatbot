package com.example.healthassistant.presentation.home

import healthassistant.composeapp.generated.resources.Res
import healthassistant.composeapp.generated.resources.ic_quickhelp_emergency
import healthassistant.composeapp.generated.resources.ic_quickhelp_history
import healthassistant.composeapp.generated.resources.ic_quickhelp_remainders
import org.jetbrains.compose.resources.DrawableResource

data class HomeState(

    // User
    val userName: String = "",

    // Greeting (can later compute based on time)
    val greetingText: String = "Good Morning",

    // Suggestion Chips
    val suggestions: List<String> = listOf(
        "I have fever",
        "Stomach hurts",
        "Coughing",
        "Headache",
        "Chest pain",
        "I'm feeling very tired from morning"
    ),

    // Quick Help Items
    val quickHelpItems: List<QuickHelpItem> = listOf(
        QuickHelpItem(
            title = "Emergency",
            description = "Get immediate help during serious health issues",
            icon = Res.drawable.ic_quickhelp_emergency
        ),
        QuickHelpItem(
            title = "Care Tip",
            description = "Take a short walk today to refresh your body and mind",
            icon = Res.drawable.ic_quickhelp_remainders
        ),
        QuickHelpItem(
            title = "Previous Check",
            description = "Review your recent health assessments",
            icon = Res.drawable.ic_quickhelp_history
        )
    )

)

data class QuickHelpItem(
    val title: String,
    val description: String,
    val icon: DrawableResource
)
