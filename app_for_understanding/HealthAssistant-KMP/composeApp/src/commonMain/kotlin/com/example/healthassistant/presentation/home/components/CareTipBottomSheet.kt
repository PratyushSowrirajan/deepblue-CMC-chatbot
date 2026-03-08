package com.example.healthassistant.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppShapes
import com.example.healthassistant.designsystem.AppTypography
import com.example.healthassistant.presentation.home.EmergencyAction
import healthassistant.composeapp.generated.resources.Res
import healthassistant.composeapp.generated.resources.ic_settings
import healthassistant.composeapp.generated.resources.img_avatar
import healthassistant.composeapp.generated.resources.img_user_avatar
import healthassistant.composeapp.generated.resources.img_walk_illustration
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareTipBottomSheet(
    tipTitle: String,
    tipMessage: String,
    reasons: List<String>,
    onSkip: () -> Unit,
    onDone: () -> Unit,
    onDismiss: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppColors.background, // same soft grey as emergency
        shape = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text = tipTitle, // "Today's Care Tip"
                style = AppTypography.poppinsSemiBold().copy(
                    fontSize = 18.sp
                ),
                color = AppColors.darkBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tipMessage, // "Take a short walk today..."
                style = AppTypography.poppinsMedium().copy(
                    fontSize = 12.sp
                ),
                color = AppColors.blue
            )

            Spacer(modifier = Modifier.height(20.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                // 🔹 LEFT SIDE — Why + Bullets
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {

                    Text(
                        text = t("Why ?"),
                        style = AppTypography.poppinsSemiBold().copy(
                            fontSize = 16.sp
                        ),
                        color = AppColors.darkBlue
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    reasons.forEach { reason ->

                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {

                            Text(
                                text = "•",
                                style = AppTypography.poppinsSemiBold(),
                                color = AppColors.blue,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = reason,
                                style = AppTypography.poppinsMedium().copy(
                                    fontSize = 12.sp
                                ),
                                color = AppColors.blue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 🔹 RIGHT SIDE — Illustration
                Image(
                    painter = painterResource(Res.drawable.img_walk_illustration),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            CareTipBottomActions(
                onSkip = onSkip,
                onDone = onDone
            )

        }
    }
}