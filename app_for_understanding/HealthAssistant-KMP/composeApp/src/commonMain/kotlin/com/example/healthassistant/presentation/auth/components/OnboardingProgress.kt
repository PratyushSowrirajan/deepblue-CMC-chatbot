package com.example.healthassistant.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingProgress(
    currentStep: Int,
    totalSteps: Int,
    title: String
) {

    val progress = currentStep.toFloat() / totalSteps.toFloat()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Step $currentStep of $totalSteps",
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}