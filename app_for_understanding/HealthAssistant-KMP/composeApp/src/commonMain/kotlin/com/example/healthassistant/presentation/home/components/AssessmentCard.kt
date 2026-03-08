package com.example.healthassistant.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppShapes
import com.example.healthassistant.designsystem.AppTypography
import androidx.compose.material3.Text
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.healthassistant.core.utils.t
import healthassistant.composeapp.generated.resources.Res
import healthassistant.composeapp.generated.resources.ic_star
import healthassistant.composeapp.generated.resources.img_doctor


@Composable
fun AssessmentCard(
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, AppShapes.extraLarge)
            .clip(AppShapes.extraLarge)
            .background(AppColors.surface)
            .padding(14.dp)
    ) {

        // 🔵 Top Blue Section - Divided into 2 Columns
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(AppShapes.large)
                .background(AppColors.blue)
        ) {

            // ========== COLUMN 1: Doctor Image ==========
            // Doctor image positioned at bottom, covering max height
            Box(
                modifier = Modifier
                    .weight(1.27f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomStart
            ) {
                Image(
                    painter = painterResource(Res.drawable.img_doctor),
                    contentDescription = t("Doctor illustration"),
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.BottomStart
                )
            }

            // ========== COLUMN 2: Text Content (2 Sub-rows) ==========
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // SUB-ROW 1: "Not feeling well?" - aligned to center of column 2
                Text(
                    text = t("Not feeling well?"),
                    style = AppTypography.poppinsMedium().copy(
                        fontSize = 16.sp
                    ),
                    color = AppColors.highlightYellow,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))

                // SUB-ROW 2: "I can help you understand your health" - aligned to center of column 2
                Text(
                    text = t("I can help you understand your health"),
                    style = AppTypography.poppinsMedium().copy(
                        fontSize = 14.sp
                    ),
                    color = AppColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔵 Start Assessment Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(AppShapes.circle)
                .background(AppColors.primaryGradient)
                .clickable { onStartClick() }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(Res.drawable.ic_star),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = t("Start Symptom Assessment"),
                style = AppTypography.poppinsSemiBold().copy(
                    fontSize = 16.sp
                ),
                color = AppColors.textSecondary
            )
        }
    }
}