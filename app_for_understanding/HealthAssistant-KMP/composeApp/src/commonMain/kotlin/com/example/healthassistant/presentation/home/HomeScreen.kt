package com.example.healthassistant.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.core.utils.LanguageState
import com.example.healthassistant.core.utils.platformInitTranslator
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.presentation.home.components.CareTipBottomSheet
import com.example.healthassistant.presentation.home.components.EmergencyBottomSheetWrapper

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onEmergencyAction: (EmergencyAction) -> Unit
) {
    val state by viewModel.state.collectAsState()

    var activeSheet by remember { mutableStateOf<HomeSheetType?>(null) }

    var showConfirmationDialog by remember { mutableStateOf(false) }

    var pendingEmergencyAction by remember { mutableStateOf<EmergencyAction?>(null) }

    var showLanguageDialog by remember { mutableStateOf(false) }


    HomeContent(
        state = state,
        onEvent = { event ->
            when (event) {

                is HomeEvent.QuickHelpClicked -> {
                    when (event.item.title) {
                        "Emergency" -> activeSheet = HomeSheetType.Emergency
                        "Care Tip" -> activeSheet = HomeSheetType.Reminder
                        "Previous Check" -> {
                            viewModel.onEvent(event)
                        }
                    }
                }

                HomeEvent.SettingsClicked -> {
                    viewModel.onEvent(HomeEvent.SettingsClicked)
                }

                else -> viewModel.onEvent(event)
            }
        }
    )

    // 🔹 Bottom Sheet Controller
    if (activeSheet != null) {

        EmergencyBottomSheetWrapper(
            visible = activeSheet == HomeSheetType.Emergency,
            onDismiss = { activeSheet = null },
            onConfirmAction = { action ->

                // Close bottom sheet
                activeSheet = null

                // Store action
                pendingEmergencyAction = action

                // Show confirmation dialog
                showConfirmationDialog = true
            }
        )
    }

    if (activeSheet == HomeSheetType.Reminder) {

        CareTipBottomSheet(
            tipTitle = t("Today's Care Tip"),
            tipMessage = t("Take a short walk today to refresh your body and mind."),
            reasons = listOf(
                t("Improves blood circulation and boosts energy."),
                t("Reduces stress and clears your mind."),
                t("Helps keep your body active.")
            ),
            onSkip = {
                activeSheet = null
            },
            onDone = {
                activeSheet = null
            },
            onDismiss = {
                activeSheet = null
            }
        )
    }

    if (showConfirmationDialog && pendingEmergencyAction != null) {

        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmationDialog = false
                        pendingEmergencyAction?.let {
                            AppLogger.d("EMERGENCY", "User confirmed action: $it")
                            onEmergencyAction(it)
                        }
                    }
                ) {
                    Text(t("YES"))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmationDialog = false
                        activeSheet = HomeSheetType.Emergency
                        AppLogger.d("EMERGENCY", "User cancelled confirmation. Reopening sheet.")
                    }
                ) {
                    Text(t("NO"))
                }
            },
            title = {
                Text(t("Confirm Action"))
            },
            text = {
                Text(t("Are you sure you want to proceed?"))
            }
        )
    }

    if (showLanguageDialog) {

        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },

            title = { Text(t("Select Language")) },

            text = {
                Column {

                    Text(
                        text = t("English"),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                LanguageState.currentLanguage.value = "en"
                                platformInitTranslator("en")
                                showLanguageDialog = false
                            }
                            .padding(12.dp)
                    )

                    Text(
                        text = t("Tamil"),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                LanguageState.currentLanguage.value = "ta"
                                platformInitTranslator("ta")
                                showLanguageDialog = false
                            }
                            .padding(12.dp)
                    )

                    Text(
                        text = t("Hindi"),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                LanguageState.currentLanguage.value = "hi"
                                platformInitTranslator("hi")
                                showLanguageDialog = false
                            }
                            .padding(12.dp)
                    )
                }
            },

            confirmButton = {}
        )
    }

}