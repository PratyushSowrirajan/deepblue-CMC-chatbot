package com.example.healthassistant.core.platform

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformBackHandler(onBack: () -> Unit)
