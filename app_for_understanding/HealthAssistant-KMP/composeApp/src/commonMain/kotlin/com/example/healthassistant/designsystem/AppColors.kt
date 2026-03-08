package com.example.healthassistant.designsystem

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppColors {

    // Brand Colors
    val heavyBlue = Color(0xFF0F2854)
    val darkBlue = Color(0xFF1C4D8D)
    val blue = Color(0xFF4988C4)
    val lightBlue = Color(0xFFBDE8F5)
    val dustyGray = Color(0xFF9CA3AF)
    val highlightYellow = Color(0xFFFFD54F)

    // BG
    val darkGray = Color(0xFF2a3855)


    // Text
    val textPrimary = heavyBlue
    val textSecondary = Color.White
    val textHint = dustyGray


    // Gradient
    val gradientStart = Color(0xFF2159BA)
    val gradientEnd = heavyBlue
    val primaryGradient: Brush
        get() = Brush.horizontalGradient(
            colors = listOf(gradientStart, gradientEnd)
        )

    val gradient2Start = AppColors.blue
    val gradient2End = AppColors.darkBlue
    val secondaryGradient: Brush
        get() = Brush.horizontalGradient(
            colors = listOf(gradientStart, gradientEnd)
        )


    // Backgrounds
    val background = Color(0xFFF2F8FF)
    val surface = Color.White



    // Button
    val primary = darkBlue
    val primaryLight = lightBlue
    val onPrimary = Color.White



    // Bottom Nav
    val navInactive = heavyBlue.copy(alpha = 0.5f)
    val navIndicator = heavyBlue
}
