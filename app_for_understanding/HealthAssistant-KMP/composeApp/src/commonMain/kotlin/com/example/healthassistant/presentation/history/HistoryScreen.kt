package com.example.healthassistant.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HistoryScreen(
    onItemClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // ───── Header ─────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "History",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Divider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )

        // ───── Content ─────
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Vertical timeline line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                HistoryItem(
                    date = "17 January 2026",
                    title = "Stomach Pain",
                    time = "04:25 PM",
                    status = HistoryStatus.DOCTOR_VISIT,
                    onClick = onItemClick
                )

                HistoryItem(
                    date = "27 December 2025",
                    title = "Head Pain",
                    time = "11:55 AM",
                    status = HistoryStatus.SELF_CARE,
                    onClick = onItemClick
                )

                HistoryItem(
                    date = "17 December 2025",
                    title = "Heart Issue",
                    time = "02:37 PM",
                    status = HistoryStatus.EMERGENCY,
                    onClick = onItemClick
                )
            }
        }
    }
}
