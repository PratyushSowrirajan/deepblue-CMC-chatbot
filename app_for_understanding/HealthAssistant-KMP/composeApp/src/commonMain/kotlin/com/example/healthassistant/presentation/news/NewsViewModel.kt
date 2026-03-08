package com.example.healthassistant.presentation.news

import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.domain.repository.NewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsViewModel(
    private val repository: NewsRepository
) {

    private val _state = MutableStateFlow(NewsState())
    val state: StateFlow<NewsState> = _state

    fun loadNews() {
        CoroutineScope(Dispatchers.Default).launch {

            AppLogger.d("NEWS_VM", "Starting news load")

            _state.update { it.copy(isLoading = true) }

            try {
                val news = repository.getHealthNews()

                AppLogger.d("NEWS_VM", "News loaded successfully: ${news.size}")

                _state.update {
                    it.copy(
                        isLoading = false,
                        articles = news
                    )
                }

            } catch (e: Exception) {

                AppLogger.d("NEWS_VM", "Error loading news: ${e.message}")

                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load news"
                    )
                }
            }
        }
    }

}

