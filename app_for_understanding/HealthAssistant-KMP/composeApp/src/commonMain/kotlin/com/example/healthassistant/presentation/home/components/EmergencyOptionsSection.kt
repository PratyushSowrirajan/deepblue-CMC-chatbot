package com.example.healthassistant.presentation.home.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.healthassistant.core.utils.t
import healthassistant.composeapp.generated.resources.Res
import healthassistant.composeapp.generated.resources.img_ambulance
import healthassistant.composeapp.generated.resources.img_familyalert

@Composable
fun EmergencyOptionsSection(
    ambulanceSelected: Boolean,
    familySelected: Boolean,
    onAmbulanceClick: () -> Unit,
    onFamilyClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 6.dp),   // ← important
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        EmergencyOptionCard(
            title = t("Ambulance (108)"),
            description = t("Call emergency ambulance immediately."),
            icon = Res.drawable.img_ambulance,
            selected = ambulanceSelected,
            onClick = onAmbulanceClick
        )

        EmergencyOptionCard(
            title = t("Alert Family"),
            description = t("Alert your family members."),
            icon = Res.drawable.img_familyalert,
            selected = familySelected,
            onClick = onFamilyClick
        )
    }
}