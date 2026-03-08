package com.example.healthassistant.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class HistoryStatus {
    DOCTOR_VISIT,
    SELF_CARE,
    EMERGENCY
}

@Composable
fun HistoryItem(
    date: String,
    title: String,
    time: String,
    status: HistoryStatus,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.Top
    ) {

        // Timeline dot
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                StatusBadge(status)
            }
        }
    }
}

@Composable
fun StatusBadge(status: HistoryStatus) {

    val (text, bgColor) = when (status) {
        HistoryStatus.DOCTOR_VISIT -> "Doctor Visit" to Color(0xFF5B9BD5)
        HistoryStatus.SELF_CARE -> "Self care" to Color(0xFF7EDFA2)
        HistoryStatus.EMERGENCY -> "Emergency" to Color(0xFFFF6B6B)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
