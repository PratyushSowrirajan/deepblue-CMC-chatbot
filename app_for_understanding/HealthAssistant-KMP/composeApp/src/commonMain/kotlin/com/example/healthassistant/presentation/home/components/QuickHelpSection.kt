package com.example.healthassistant.presentation.home.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.healthassistant.presentation.home.QuickHelpItem

@Composable
fun QuickHelpSection(
    items: List<QuickHelpItem>,
    onItemClick: (QuickHelpItem) -> Unit
) {

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item ->
            QuickHelpCard(
                item = item,
                onClick = { onItemClick(item) }
            )
        }
    }
}
