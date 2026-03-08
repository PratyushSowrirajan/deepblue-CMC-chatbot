package com.example.healthassistant.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
expect fun drawablePainter(name: String): Painter