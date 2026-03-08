package com.example.healthassistant.presentation.news

import com.example.healthassistant.domain.model.news.NewsArticle

data class NewsState(
    val isLoading: Boolean = false,
    val articles: List<NewsArticle> = emptyList(),
    val error: String? = null
)
