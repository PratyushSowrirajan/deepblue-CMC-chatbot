package com.example.healthassistant.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppShapes
import com.example.healthassistant.designsystem.AppTypography
import com.example.healthassistant.presentation.home.QuickHelpItem
import org.jetbrains.compose.resources.painterResource

@Composable
fun QuickHelpCard(
    item: QuickHelpItem,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .width(160.dp)
            .fillMaxHeight()
            .shadow(
                elevation = 2.dp,
                shape = AppShapes.extraLarge
            )
            .clip(AppShapes.extraLarge)
            .background(AppColors.surface)
            .clickable { onClick() }
    ) {

        // 🔵 TOP IMAGE AREA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.58f)
                .background(AppColors.lightBlue),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(item.icon),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentScale = ContentScale.Fit
            )

        }

        // ⚪ BOTTOM TEXT AREA
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(AppColors.surface)
                .padding(16.dp)
        ) {

            Text(
                text = t(item.title),
                style = AppTypography.poppinsRegular().copy(
                    fontSize = 14.sp
                ),
                color = AppColors.heavyBlue
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = t(item.description),
                style = AppTypography.poppinsMedium().copy(
                    fontSize = 10.sp
                ),
                color = AppColors.blue
            )
        }
    }
}

