package com.example.healthassistant.presentation.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppShapes
import com.example.healthassistant.designsystem.AppTypography
import healthassistant.composeapp.generated.resources.Res
import healthassistant.composeapp.generated.resources.img_user_avatar
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun EmergencyOptionCard(
    title: String,
    description: String,
    icon: DrawableResource,
    selected: Boolean,
    onClick: () -> Unit
) {

    val strokeColor =
        if (selected)
            AppColors.blue.copy(alpha = 0.8f)
        else
            AppColors.blue.copy(alpha = 0.25f)

    val animatedElevation by animateDpAsState(
        targetValue = if (selected) 8.dp else 0.dp,
        label = "cardElevation"
    )


    Card(
        modifier = Modifier
            .width(280.dp)
            .animateContentSize()
            .clickable { onClick() },
        shape = AppShapes.large,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = strokeColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = animatedElevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.surface
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = title,
                style = AppTypography.poppinsSemiBold().copy(
                    fontSize = 14.sp
                ),
                color = AppColors.blue
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = description,
                    style = AppTypography.poppinsMedium().copy(
                        fontSize = 12.sp
                    ),
                    color = AppColors.darkBlue
                )
            }
        }
    }
}
