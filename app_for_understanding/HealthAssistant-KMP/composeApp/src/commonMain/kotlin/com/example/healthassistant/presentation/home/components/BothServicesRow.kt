package com.example.healthassistant.presentation.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppTypography
import healthassistant.composeapp.generated.resources.Res
import healthassistant.composeapp.generated.resources.ic_check
import healthassistant.composeapp.generated.resources.ic_settings
import healthassistant.composeapp.generated.resources.img_avatar
import healthassistant.composeapp.generated.resources.img_user_avatar
import org.jetbrains.compose.resources.painterResource

@Composable
fun BothServicesRow(
    ambulanceSelected: Boolean,
    familySelected: Boolean,
    onToggleBoth: () -> Unit
) {

    val bothSelected = ambulanceSelected && familySelected

    val animatedBorderColor by animateColorAsState(
        targetValue = if (bothSelected) AppColors.blue else AppColors.blue.copy(alpha = 0.4f),
        label = "checkboxBorder"
    )

    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (bothSelected) AppColors.blue else Color.Transparent,
        label = "checkboxBackground"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleBoth() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Checkbox
        Box(
            modifier = Modifier
                .size(18.dp)
                .border(
                    width = 1.dp,
                    color = AppColors.blue,
                    shape = RoundedCornerShape(6.dp)
                )
                .background(
                    color = animatedBackgroundColor,
                    shape = RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            if (bothSelected) {
                Icon(
                    painter = painterResource(Res.drawable.ic_check),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = t("Call ambulance and notify family"),
            style = AppTypography.poppinsMedium().copy(fontSize = 12.sp),
            color = AppColors.blue
        )
    }
}