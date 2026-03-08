package com.example.healthassistant.presentation.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.healthassistant.presentation.auth.model.QuestionUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionInput(
    question: QuestionUiModel,
    onValueChange: (String) -> Unit,
    isRequiredError: Boolean
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        when (question.type) {

            // -----------------------
            // TEXT INPUT
            // -----------------------

            "text" -> {

                OutlinedTextField(
                    value = question.value ?: "",
                    onValueChange = { onValueChange(it) },
                    label = { Text(question.questionText) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isRequiredError
                )
            }

            // -----------------------
            // NUMBER INPUT
            // -----------------------

            "number" -> {

                OutlinedTextField(
                    value = question.value ?: "",
                    onValueChange = { onValueChange(it) },
                    label = { Text(question.questionText) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    isError = isRequiredError
                )
            }

            // -----------------------
            // DROPDOWN INPUT
            // -----------------------

            "single_choice" -> {

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {

                    OutlinedTextField(
                        value = question.value ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(question.questionText) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        isError = isRequiredError
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {

                        question.options.forEach { option ->

                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onValueChange(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // -----------------------
        // REQUIRED ERROR TEXT
        // -----------------------

        if (isRequiredError) {

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Required",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(12.dp))
    }
}