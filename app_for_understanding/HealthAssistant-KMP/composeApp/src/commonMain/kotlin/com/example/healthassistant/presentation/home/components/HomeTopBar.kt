package com.example.healthassistant.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppShapes
import healthassistant.composeapp.generated.resources.Res
import healthassistant.composeapp.generated.resources.ic_settings
import healthassistant.composeapp.generated.resources.img_user_avatar
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeTopBar(
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 👤 Avatar
        Image(
            painter = painterResource(Res.drawable.img_user_avatar),
            contentDescription = t("User Avatar"),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(44.dp)
                .clip(AppShapes.circle)
                .border(
                    width = 1.dp,
                    color = AppColors.blue,
                    shape = AppShapes.circle
                )
        )


        // ⚙ Settings
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(AppShapes.medium)
                .background(AppColors.surface)
                .clickable { onSettingsClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_settings), // replace with your asset
                contentDescription = t("Settings"),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
