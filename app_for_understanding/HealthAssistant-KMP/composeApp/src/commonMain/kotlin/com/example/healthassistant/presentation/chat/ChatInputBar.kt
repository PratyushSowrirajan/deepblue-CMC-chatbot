package com.example.healthassistant.presentation.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChatInputBar(
    typedMessage: String,
    isLoading: Boolean,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {

        OutlinedTextField(
            value = typedMessage,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Ask Remy...") },
            singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onSendClick,
            enabled = typedMessage.isNotBlank() && !isLoading
        ) {
            Text("Send")
        }
    }
}
