package com.example.healthassistant.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppShapes
import com.example.healthassistant.designsystem.AppTypography

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuggestionChips(
    suggestions: List<String>,
    onClick: (String) -> Unit
) {

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        suggestions.forEach { text ->

            Box(
                modifier = Modifier
                    .clip(AppShapes.large)
                    .background(AppColors.blue)
                    .clickable { onClick(text) }
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = t(text),
                    style = AppTypography.poppinsMedium().copy(
                        fontSize = 11.sp
                    ),
                    color = AppColors.textSecondary
                )
            }
        }
    }
}
