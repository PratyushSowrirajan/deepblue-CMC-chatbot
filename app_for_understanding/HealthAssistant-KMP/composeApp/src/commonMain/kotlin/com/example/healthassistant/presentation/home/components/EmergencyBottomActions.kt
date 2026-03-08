package com.example.healthassistant.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppTypography
import com.example.healthassistant.presentation.home.EmergencyAction
import healthassistant.composeapp.generated.resources.Res
import healthassistant.composeapp.generated.resources.ic_close
import healthassistant.composeapp.generated.resources.ic_headset
import healthassistant.composeapp.generated.resources.ic_home
import healthassistant.composeapp.generated.resources.img_avatar
import healthassistant.composeapp.generated.resources.img_user_avatar
import org.jetbrains.compose.resources.painterResource
@Composable
fun EmergencyBottomActions(
    ambulanceSelected: Boolean,
    familySelected: Boolean,
    onCancel: () -> Unit,
    onConfirm: (EmergencyAction) -> Unit
) {

    val isEnabled = ambulanceSelected || familySelected

    val actionText = when {
        ambulanceSelected && familySelected -> t("Proceed")
        ambulanceSelected -> t("Call 108")
        familySelected -> t("Alert")
        else -> t("Select Service")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(28.dp))
//            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .border(
                width = 1.dp,
                color = AppColors.darkBlue.copy(alpha = 0.25f),
                shape = RoundedCornerShape(28.dp)
//                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            )
    ) {

        // 🔹 Cancel Side
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { onCancel() },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(Res.drawable.ic_close), // use your close icon
                contentDescription = null,
                tint = AppColors.dustyGray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = t("Cancel"),
                style = AppTypography.poppinsSemiBold().copy(fontSize = 14.sp),
                color = AppColors.dustyGray
            )
        }

        // 🔹 Vertical Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(AppColors.darkBlue.copy(alpha = 0.25f))
        )

        // 🔹 Confirm Side
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable(enabled = isEnabled) {
                    val action = when {
                        ambulanceSelected && familySelected -> EmergencyAction.Both
                        ambulanceSelected -> EmergencyAction.Call108
                        familySelected -> EmergencyAction.AlertFamily
                        else -> null
                    }
                    action?.let { onConfirm(it) }
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(Res.drawable.ic_headset), // your call icon
                contentDescription = null,
                tint = if (isEnabled) AppColors.blue else AppColors.blue.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = actionText,
                style = AppTypography.poppinsSemiBold().copy(fontSize = 14.sp),
                color = if (isEnabled)
                    AppColors.blue
                else
                    AppColors.blue.copy(alpha = 0.5f)
            )
        }
    }
}