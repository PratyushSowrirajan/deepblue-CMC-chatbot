package com.example.healthassistant.presentation.assessment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BottomAssessmentControls(
    isMuted: Boolean,
    isMicOn: Boolean,
    onMicClick: () -> Unit,
    onVolumeClick: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 28.dp),
        contentAlignment = Alignment.Center
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🎥 Video
            BottomSoftIconButton(
                icon = Icons.Default.Videocam,
                shape = RoundedCornerShape(
                    topStart = 28.dp,
                    bottomStart = 28.dp,
                    topEnd = 18.dp,
                    bottomEnd = 18.dp
                ),
                isActive = false,
                onClick = {}
            )

            // 🎤 Mic
            BottomSoftIconButton(
                icon = if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff,
                shape = RoundedCornerShape(20.dp),
                isActive = isMicOn,
                onClick = onMicClick
            )

            // 🔊 Volume
            BottomSoftIconButton(
                icon = if (isMuted)
                    Icons.Default.VolumeOff
                else
                    Icons.Default.VolumeUp,
                shape = RoundedCornerShape(20.dp),
                isActive = !isMuted,
                onClick = onVolumeClick
            )

            // ⋮ More
            BottomSoftIconButton(
                icon = Icons.Default.MoreVert,
                shape = RoundedCornerShape(
                    topEnd = 28.dp,
                    bottomEnd = 28.dp,
                    topStart = 18.dp,
                    bottomStart = 18.dp
                ),
                isActive = false,
                onClick = {}
            )
        }
    }
}

@Composable
private fun BottomSoftIconButton(
    icon: ImageVector,
    shape: Shape,
    isActive: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor =
        if (isActive) Color(0xFF1C4D8D) else Color.White

    val iconColor =
        if (isActive) Color.White else Color(0xFF1C4D8D)

    Box(
        modifier = Modifier
            .size(66.dp)
            .background(
                color = backgroundColor,
                shape = shape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor
        )
    }
}