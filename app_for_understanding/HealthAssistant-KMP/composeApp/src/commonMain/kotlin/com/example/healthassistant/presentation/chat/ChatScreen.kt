package com.example.healthassistant.presentation.chat


import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.ui.graphics.graphicsLayer
import com.example.healthassistant.core.platform.PlatformBackHandler
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppTypography
import com.example.healthassistant.domain.model.chat.Role
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    currentReportId: String?,
    onBack: () -> Unit
) {

    val state by viewModel.state.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }

    PlatformBackHandler {
        showExitDialog = true
    }


    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Start chat
    // Start chat safely
    LaunchedEffect(currentReportId) {
        if (state.sessionId == null) {
            viewModel.startChat(currentReportId)
        }
    }

    // Auto scroll
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(state.messages.lastIndex)
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(t("End Chat Session")) },
            text = { Text(t("Warning: This will end the chat session.")) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    viewModel.endChat()
                    onBack()
                }) { Text(t("End"), color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(t("Cancel"))
                }
            }
        )
    }

    // 🔥 IMPORTANT: Use Box Layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            ChatTopbar(onClose = { showExitDialog = true })

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(
                    top = 12.dp,
                    bottom = 80.dp // space for input
                )
            ) {

                items(state.messages) { message ->
                    PremiumChatBubble(
                        text = t(message.content),
                        isUser = message.role == Role.USER
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (state.isLoading) {
                    item { TypingIndicator() }
                }
            }
        }

        // 🔥 FLOATING INPUT
        PremiumChatInput(
            value = state.typedMessage,
            isLoading = state.isLoading,
            isListening = state.isListening,
            isTtsEnabled = state.isTtsEnabled,
            onValueChange = {
                viewModel.onEvent(ChatEvent.MessageChanged(it))
            },
            onSend = {
                viewModel.onEvent(ChatEvent.SendMessage)
            },
            onMicClick = {
                viewModel.onEvent(ChatEvent.ToggleMic)
            },
            onTtsClick = {
                viewModel.onEvent(ChatEvent.ToggleTts)
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun PremiumChatBubble(
    text: String,
    isUser: Boolean
) {

    val bubbleColor =
        if (isUser) AppColors.darkBlue
        else Color(0xFFF2F4F8)

    val textColor =
        if (isUser) Color.White
        else AppColors.heavyBlue

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (isUser) Arrangement.End
            else Arrangement.Start
    ) {

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 22.dp,
                        topEnd = 22.dp,
                        bottomStart = if (isUser) 22.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 22.dp
                    )
                )
                .background(bubbleColor)
                .padding(14.dp)
        ) {
            Text(
                text = t(text),
                color = textColor,
                style = AppTypography.poppinsSemiBold().copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun TypingIndicator() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFF2F4F8))
                .padding(16.dp)
        ) {
            Text(
                text = t("Remy is typing..."),
                style = AppTypography.poppinsSemiBold().copy(
                    fontSize = 13.sp
                ),
                color = AppColors.heavyBlue
            )
        }
    }
}

@Composable
fun PremiumChatInput(
    value: String,
    isLoading: Boolean,
    isListening: Boolean,
    isTtsEnabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit,
    onTtsClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val pulse by rememberInfiniteTransition(label = "micPulse").animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Surface(
        shadowElevation = 10.dp,
        tonalElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🎤 MIC BUTTON
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    }
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        if (isListening)
                            AppColors.darkBlue
                        else
                            Color(0xFFF1F5F9)
                    )
                    .clickable { onMicClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isListening)
                        Icons.Default.Mic
                    else
                        Icons.Default.MicOff,
                    contentDescription = null,
                    tint =
                        if (isListening)
                            Color.White
                        else
                            AppColors.darkBlue
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 🔊 TTS BUTTON
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        if (isTtsEnabled)
                            AppColors.darkBlue
                        else
                            Color(0xFFF1F5F9)
                    )
                    .clickable { onTtsClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isTtsEnabled)
                        Icons.Default.VolumeUp
                    else
                        Icons.Default.VolumeOff,
                    contentDescription = null,
                    tint =
                        if (isTtsEnabled)
                            Color.White
                        else
                            AppColors.darkBlue
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // ✍ TEXT FIELD
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(t("Message Remy...")) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(28.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.darkBlue,
                    unfocusedBorderColor = Color(0xFFCBD5E1)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 🚀 SEND BUTTON
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (value.isNotBlank() && !isLoading)
                            AppColors.darkBlue
                        else
                            Color(0xFFE2E8F0)
                    )
                    .clickable(
                        enabled = value.isNotBlank() && !isLoading
                    ) { onSend() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint =
                        if (value.isNotBlank() && !isLoading)
                            Color.White
                        else
                            Color.Gray
                )
            }
        }
    }
}