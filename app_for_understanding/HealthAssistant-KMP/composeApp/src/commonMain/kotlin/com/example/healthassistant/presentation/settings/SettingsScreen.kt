package com.example.healthassistant.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.healthassistant.core.utils.t

data class SettingsItem(
    val title: String
)

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onProfileClick: () -> Unit,
    onMedicalClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    val items = listOf(
        SettingsItem("Profile Data"),
        SettingsItem("Medical Data"),
        SettingsItem("Language"),
        SettingsItem("Logout")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = t("Settings"),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {

            items(items) { item ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            when (item.title) {

                                "Profile Data" -> onProfileClick()

                                "Medical Data" -> onMedicalClick()

                                "Language" -> onLanguageClick()

                                "Logout" -> onLogoutClick()
                            }
                        }
                        .padding(vertical = 16.dp)
                ) {
                    Text(text = t(item.title))
                }

                Divider()
            }
        }
    }
}