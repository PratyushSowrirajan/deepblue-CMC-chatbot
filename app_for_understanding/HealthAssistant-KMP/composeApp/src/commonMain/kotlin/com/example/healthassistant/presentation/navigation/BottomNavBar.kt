package com.example.healthassistant.presentation.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppShapes
import com.example.healthassistant.designsystem.AppTypography
import healthassistant.composeapp.generated.resources.Res
import healthassistant.composeapp.generated.resources.ic_history
import healthassistant.composeapp.generated.resources.ic_home
import healthassistant.composeapp.generated.resources.ic_news
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun BottomNavBar(
    selected: AppScreen,
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onNewsClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.surface)
            .navigationBarsPadding()
    ) {

        // 🔹 LAYER 1 — Indicator Line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
        ) {

            IndicatorSegment(selected == AppScreen.Home)
            IndicatorSegment(selected == AppScreen.History)
            IndicatorSegment(selected == AppScreen.News)
        }

        // 🔹 LAYER 2 — Navigation Items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            BottomNavItem(
                icon = Res.drawable.ic_home,
                label = "Home",
                selected = selected == AppScreen.Home,
                onClick = onHomeClick
            )

            BottomNavItem(
                icon = Res.drawable.ic_history,
                label = "History",
                selected = selected == AppScreen.History,
                onClick = onHistoryClick
            )

            BottomNavItem(
                icon = Res.drawable.ic_news,
                label = "News",
                selected = selected == AppScreen.News,
                onClick = onNewsClick
            )
        }
    }
}

@Composable
private fun RowScope.IndicatorSegment(
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(
                if (selected)
                    AppColors.darkBlue
                else
                    AppColors.darkBlue.copy(alpha = 0.2f)
            )
    )
}


@Composable
private fun RowScope.BottomNavItem(
    icon: DrawableResource,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }

    // 🔹 Animated width (bubble stretch)
    val bubbleWidth by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (selected) 64.dp else 32.dp,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = 0.7f,
            stiffness = 500f
        ),
        label = "bubbleWidth"
    )

    // 🔹 Animated background fade
    val bgColor by animateColorAsState(
        targetValue = if (selected)
            AppColors.lightBlue.copy(alpha = 0.4f)
        else
            androidx.compose.ui.graphics.Color.Transparent,
        animationSpec = tween(250),
        label = "bgAnim"
    )

    // 🔹 Animated icon/text color
    val contentColor by animateColorAsState(
        targetValue = if (selected)
            AppColors.darkBlue
        else
            AppColors.darkBlue.copy(alpha = 0.6f),
        animationSpec = tween(250),
        label = "colorAnim"
    )

    // 🔹 Animate horizontal padding instead of width
    val horizontalPadding by animateDpAsState(
        targetValue = if (selected) 8.dp else 20.dp,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = 600f
        ),
        label = "bubblePadding"
    )


    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
                    .height(32.dp)
                    .clip(AppShapes.large)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = label,
                    modifier = Modifier.size(22.dp),
                    colorFilter = ColorFilter.tint(contentColor)
                )
            }
        }



        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = AppTypography.bodySmall(),
            color = contentColor
        )
    }
}




