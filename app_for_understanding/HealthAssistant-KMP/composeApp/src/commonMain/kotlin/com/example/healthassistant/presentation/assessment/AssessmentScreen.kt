package com.example.healthassistant.presentation.assessment



import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.healthassistant.core.image.ImagePickerManager
import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.domain.model.assessment.ResponseOption
import com.example.healthassistant.presentation.assessment.components.*

@Composable
fun AssessmentScreen(
    viewModel: AssessmentViewModel,
    onExit: () -> Unit,
    onReportGenerated: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    var showExitDialog by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }

    LaunchedEffect(state.report) {
        if (state.report != null) {
            onReportGenerated()
        }
    }

    LaunchedEffect(state.sessionId) {
        if (state.sessionId.isEmpty()) {
            AppLogger.d("UI", "AssessmentScreen launched → startAssessment()")
            viewModel.startAssessment()
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(t("End Assessment")) },
            text = { Text(t("Assessment will be ended. Do you want to continue?")) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        viewModel.endAssessment { onExit() }
                    }
                ) { Text(t("YES"), color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(t("Cancel"))
                }
            }
        )
    }

    LaunchedEffect(state.isLoading, state.isGeneratingReport) {
        if (state.isLoading || state.isGeneratingReport) {
            kotlinx.coroutines.delay(1000)
            if (state.isLoading || state.isGeneratingReport) {
                showLoading = true
            }
        } else {
            showLoading = false
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {

            // ---------- VISUAL MODE ----------
            if (state.isVisualModeActive) {

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    AssessmentTopHeader(
                        onClose = { viewModel.onEvent(AssessmentEvent.CloseVisualMode) }
                    )

                    BodySelector(
                        onBodyPartClick = { bodyPart ->
                            viewModel.onEvent(
                                AssessmentEvent.BodyPartSelected(bodyPart)
                            )
                        }
                    )
                }

            } else {

                // ---------- NORMAL MODE ----------
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    AssessmentTopHeader(
                        onClose = { showExitDialog = true }
                    )

                    Spacer(Modifier.height(32.dp))

                    AvatarSection(
                        isMuted = state.isMuted,
                        isSpeaking = !state.isMuted
                    )

                    Spacer(Modifier.height(32.dp))

                    state.currentQuestion?.let { question ->

                        val isSymptomQuestion =
                            question.id == "q_current_ailment"

                        QuestionSection(text = question.text)

                        Spacer(Modifier.height(28.dp))

                        // IMAGE QUESTION
                        if (state.isImageQuestion) {

                            val imagePicker = remember {
                                ImagePickerManager(viewModel::onImageSelected)
                            }

                            imagePicker.RenderPickerButton()

                            if (state.selectedImageBytes != null) {

                                Spacer(Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        viewModel.onEvent(
                                            AssessmentEvent.SendImage
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Text("Submit Image")
                                }
                            }

                        } else {

                            when (question.responseType) {

                                "text", "number" -> {

                                    GradientTextInput(
                                        value = state.typedText,
                                        onValueChange = {
                                            viewModel.onEvent(
                                                AssessmentEvent.TextChanged(it)
                                            )
                                        },
                                        onSubmit = {
                                            viewModel.onEvent(
                                                AssessmentEvent.SendText
                                            )
                                        }
                                    )
                                }

                                "single_choice", "multi_choice" -> {

                                    val options =
                                        question.responseOptions ?: emptyList()

                                    if (options.size <= 4) {

                                        options.forEach { option ->

                                            GradientOptionButton(
                                                text = t(option.label),
                                                onClick = {
                                                    if (!state.isSubmitting) {
                                                        viewModel.onEvent(
                                                            AssessmentEvent.OptionSelected(option.id)
                                                        )
                                                    }
                                                }
                                            )

                                            Spacer(Modifier.height(16.dp))
                                        }

                                    } else {

                                        StyledDropdown(
                                            options = options,
                                            questionId = question.id,
                                            onOptionSelected = { selectedId ->
                                                if (!state.isSubmitting) {
                                                    viewModel.onEvent(
                                                        AssessmentEvent.OptionSelected(selectedId)
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // POINT ON BODY BUTTON
                        if (isSymptomQuestion) {

                            Spacer(Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    viewModel.onEvent(
                                        AssessmentEvent.OpenVisualMode
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("Point on the body")
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))
                }

                BottomAssessmentControls(
                    isMuted = state.isMuted,
                    isMicOn = state.isListening,
                    onMicClick = {
                        viewModel.onEvent(AssessmentEvent.MicClicked)
                    },
                    onVolumeClick = {
                        viewModel.onEvent(AssessmentEvent.VolumeClicked)
                    },
                    onExit = { showExitDialog = true },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            AnimatedVisibility(visible = showLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
                        )
                        .pointerInput(Unit) {},
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text =
                                if (state.isGeneratingReport)
                                    t("Generating your medical report...")
                                else
                                    t("Preparing next question...")
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledDropdown(
    options: List<ResponseOption>,
    questionId: String,
    onOptionSelected: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedLabel by remember(questionId) {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = t(selectedLabel),
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    if (selectedLabel.isEmpty())
                        t("Select an option")
                    else
                        t(selectedLabel)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5A8DEE),
                unfocusedBorderColor = Color(0xFFB0C4FF)
            )
        )

        // 👇 Invisible click layer over entire field
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 250.dp)
        ) {

            options.forEach { option ->

                DropdownMenuItem(
                    text = { Text(t(option.label)) },
                    onClick = {
                        selectedLabel = option.label
                        expanded = false
                        onOptionSelected(option.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun ActionIcon(
    icon: ImageVector,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ChooseUserScreen(
    onMyself: () -> Unit,
    onSomeoneElse: () -> Unit
) {
    Column {
        Text("For whom are you taking this assessment?")

        Button(onClick = onMyself) {
            Text("Myself")
        }

        Button(onClick = onSomeoneElse) {
            Text("Someone else")
        }
    }
}


