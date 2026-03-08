package com.example.healthassistant.domain.model.news

data class NewsArticle(
    val title: String,
    val description: String,
    val imageUrl: String?,
    val publishedTime: String,
    val sourceName: String
)
