package com.example.healthassistant.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppTypography

@Composable
fun GreetingSection(
    greetingText: String,
    userName: String
) {
    Column {

        Text(
            text = t("$greetingText, $userName"),
            style = AppTypography.poppinsMedium().copy(
                fontSize = 14.sp
            ),
            color = AppColors.textHint
        )

        Spacer(modifier = Modifier.height(0.dp))

        Text(
            text = t("How Are You Today ?"),
            style = AppTypography.poppinsSemiBold().copy(
                fontSize = 24.sp
            ),
            color = AppColors.textPrimary
        )
    }
}
