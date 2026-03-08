package com.example.healthassistant.presentation.news

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppTypography

@Composable
fun NewsScreen(
    viewModel: NewsViewModel
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.articles.isEmpty()) {
        if (state.articles.isEmpty()) {
            viewModel.loadNews()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.background)
            .statusBarsPadding()
    ) {

        // 🔹 Top Bar
        NewsTopBar()

        // 🔹 News List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 12.dp
            )
        ) {


            items(state.articles) { article ->

                NewsItem(
                    description = article.description,
                    postedTime = article.publishedTime,
                    imageUrl = article.imageUrl,
                    sourceName = article.sourceName
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun NewsTopBar() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.background)   // Make top bar background
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = t("Health News"),
                style = AppTypography.poppinsSemiBold().copy(
                    fontSize = 24.sp
                ),
                color = AppColors.textPrimary
            )
        }

        // 🔹 Divider BELOW title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AppColors.blue)
        )
    }
}
