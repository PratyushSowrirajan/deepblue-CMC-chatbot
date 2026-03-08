package com.example.healthassistant.data.remote.news.dto

import kotlinx.serialization.Serializable

@Serializable
data class NewsResponseDto(
    val status: String,
    val totalResults: Int? = null,
    val articles: List<ArticleDto>? = null,
    val code: String? = null,
    val message: String? = null
)

@Serializable
data class ArticleDto(
    val source: SourceDto? = null,
    val title: String? = null,
    val description: String? = null,
    val urlToImage: String? = null,
    val publishedAt: String? = null
)

@Serializable
data class SourceDto(
    val id: String? = null,
    val name: String? = null
)
