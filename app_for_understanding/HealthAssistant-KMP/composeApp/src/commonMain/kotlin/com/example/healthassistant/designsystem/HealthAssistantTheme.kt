package com.example.healthassistant.designsystem

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun HealthAssistantTheme(
    content: @Composable () -> Unit
) {

    val colorScheme = lightColorScheme(
        primary = AppColors.primary,
        onPrimary = AppColors.onPrimary,
        background = AppColors.background,
        surface = AppColors.surface,
        onBackground = AppColors.textPrimary
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(), // We use AppTypography manually
        shapes = Shapes(),
        content = content
    )
}
