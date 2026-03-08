package com.example.healthassistant.presentation.home.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.presentation.home.EmergencyAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyBottomSheetWrapper(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirmAction: (EmergencyAction) -> Unit
) {

    if (!visible) return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.background, // soft grey like img1
        shape = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp
        )
    ) {

        EmergencyBottomSheetContent(
            onDismiss = onDismiss,
            onConfirmAction = onConfirmAction
        )
    }
}