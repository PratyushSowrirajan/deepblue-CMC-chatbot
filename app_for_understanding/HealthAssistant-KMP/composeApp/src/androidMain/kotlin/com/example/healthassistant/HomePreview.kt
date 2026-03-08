package com.example.healthassistant

import NewsApiImpl
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.core.network.NetworkClient
import com.example.healthassistant.data.repository.NewsRepositoryImpl
import com.example.healthassistant.designsystem.HealthAssistantTheme
import com.example.healthassistant.presentation.home.HomeContent
import com.example.healthassistant.presentation.home.HomeState
import com.example.healthassistant.presentation.news.NewsScreen
import com.example.healthassistant.presentation.news.NewsViewModel

@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun HomePreview() {

    HealthAssistantTheme {
        HomeContent(
            state = HomeState(
                userName = "Gowtham"
            ),
            onEvent = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun NewsPreview() {

    HealthAssistantTheme {
        val newsApi = remember {
            NewsApiImpl(
                client = NetworkClient.httpClient,
                apiKey = "vdv"
            )
        }

        val newsRepository = remember {
            NewsRepositoryImpl(newsApi)
        }
        val newsViewModel = remember {
            NewsViewModel(newsRepository)
        }
        NewsScreen(
            viewModel = newsViewModel
        )
    }
}

