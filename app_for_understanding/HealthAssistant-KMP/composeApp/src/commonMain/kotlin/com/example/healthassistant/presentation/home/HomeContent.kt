package com.example.healthassistant.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppShapes
import com.example.healthassistant.designsystem.AppTypography
import com.example.healthassistant.presentation.home.components.AssessmentCard
import com.example.healthassistant.presentation.home.components.GreetingSection
import com.example.healthassistant.presentation.home.components.HomeTopBar
import com.example.healthassistant.presentation.home.components.QuickHelpSection
import com.example.healthassistant.presentation.home.components.RemyChatCard
import com.example.healthassistant.presentation.home.components.SuggestionChips

@Composable
fun HomeContent(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
    ) {

//        Spacer(modifier = Modifier.height(12.dp))

        HomeTopBar(
            onSettingsClick = {
                onEvent(HomeEvent.SettingsClicked)
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        GreetingSection(
            greetingText = state.greetingText,
            userName = state.userName
        )

        Spacer(modifier = Modifier.height(12.dp))

        AssessmentCard(
            onStartClick = {
                onEvent(HomeEvent.StartAssessment)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = t("Remy Chat"),
            style = AppTypography.poppinsSemiBold().copy(
                fontSize = 18.sp
            ),
            color = AppColors.textPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        RemyChatCard(
            onAskClick = {
                onEvent(HomeEvent.OpenChat)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = t("You Can Ask Like This"),
            style = AppTypography.poppinsMedium().copy(
                fontSize = 12.sp
            ),
            color = AppColors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        SuggestionChips(
            suggestions = state.suggestions,
            onClick = {
                onEvent(HomeEvent.SuggestionClicked(it))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = t("Quick Help"),
            style = AppTypography.poppinsSemiBold().copy(
                fontSize = 18.sp
            ),
            color = AppColors.textPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        QuickHelpSection(
            items = state.quickHelpItems,
            onItemClick = {
                onEvent(HomeEvent.QuickHelpClicked(it))
            }
        )

        Spacer(modifier = Modifier.height(0.dp)) // bottom breathing space



    }
}
