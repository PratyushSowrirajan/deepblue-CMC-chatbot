package com.example.healthassistant.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.healthassistant.core.logger.AppLogger

@Composable
fun SignupScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onSignupSuccess: () -> Unit
) {
    val state = viewModel.state.value


    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            AppLogger.d("SIGNUP_SCREEN", "Navigating to Profile")
            onSignupSuccess()
            viewModel.resetSuccess()   // add reset function
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Signup", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(AuthEvent.OnEmailChange(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(AuthEvent.OnPasswordChange(it)) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = { viewModel.onEvent(AuthEvent.OnConfirmPasswordChange(it)) },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.onEvent(AuthEvent.OnSignupClick) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Signup")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Already have account? Login")
        }

        state.errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}