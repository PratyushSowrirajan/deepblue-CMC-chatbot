package com.example.healthassistant.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppShapes
import com.example.healthassistant.designsystem.AppTypography

@Composable
fun RemyChatCard(
    onAskClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, AppShapes.large)
            .clip(AppShapes.large)
            .background(AppColors.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = t("Get Instant Health Advice"),
            style = AppTypography.poppinsRegular().copy(
                fontSize = 14.sp
            ),
            color = AppColors.textPrimary,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(40))
                .background(AppColors.darkGray)
                .clickable { onAskClick() }
                .padding(horizontal = 32.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = t("Ask Remy Ai"),
                style = AppTypography.poppinsSemiBold().copy(
                    fontSize = 14.sp
                ),
                color = AppColors.surface
            )
        }
    }
}