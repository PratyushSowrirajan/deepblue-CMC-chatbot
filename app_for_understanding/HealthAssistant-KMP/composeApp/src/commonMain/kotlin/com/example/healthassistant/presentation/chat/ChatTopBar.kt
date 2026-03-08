package com.example.healthassistant.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


import androidx.compose.ui.unit.sp
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppTypography
import healthassistant.composeapp.generated.resources.Res
import healthassistant.composeapp.generated.resources.ic_close
import org.jetbrains.compose.resources.painterResource

@Composable
fun ChatTopbar(
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .fillMaxWidth()
            .statusBarsPadding()   // 🔥 important
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = t("Remy Ai"),
                style = AppTypography.poppinsSemiBold().copy(
                    fontSize = 20.sp
                ),
                color = AppColors.darkBlue
            )

            Icon(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = "Close",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onClose() },
                tint = AppColors.darkBlue
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Blue underline
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}