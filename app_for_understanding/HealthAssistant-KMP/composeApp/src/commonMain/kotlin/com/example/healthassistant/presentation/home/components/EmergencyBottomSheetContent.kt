package com.example.healthassistant.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
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
import com.example.healthassistant.designsystem.AppTypography
import com.example.healthassistant.presentation.home.EmergencyAction

@Composable
fun EmergencyBottomSheetContent(
    onDismiss: () -> Unit,
    onConfirmAction: (EmergencyAction) -> Unit
) {

    var ambulanceSelected by remember { mutableStateOf(false) }
    var familySelected by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Title
            Text(
                text = t("Emergency Assistance"),
                style = AppTypography.poppinsSemiBold().copy(
                    fontSize = 18.sp
                ),
                color = AppColors.darkBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = t("If this is a medical emergency, act immediately"),
                style = AppTypography.poppinsMedium().copy(
                    fontSize = 12.sp
                ),
                color = AppColors.dustyGray
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        EmergencyOptionsSection(
            ambulanceSelected = ambulanceSelected,
            familySelected = familySelected,
            onAmbulanceClick = {
                ambulanceSelected = !ambulanceSelected
            },
            onFamilyClick = {
                familySelected = !familySelected
            }
        )

        Spacer(modifier = Modifier.height(2.dp))

        BothServicesRow(
            ambulanceSelected = ambulanceSelected,
            familySelected = familySelected,
            onToggleBoth = {

                val bothSelected = ambulanceSelected && familySelected

                if (bothSelected) {
                    // Deselect both
                    ambulanceSelected = false
                    familySelected = false
                } else {
                    // Select both
                    ambulanceSelected = true
                    familySelected = true
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        EmergencyBottomActions(
            ambulanceSelected = ambulanceSelected,
            familySelected = familySelected,
            onCancel = onDismiss,
            onConfirm = { action ->
                onConfirmAction(action)
            }
        )
    }
}