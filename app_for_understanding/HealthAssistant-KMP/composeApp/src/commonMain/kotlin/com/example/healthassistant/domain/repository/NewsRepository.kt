package com.example.healthassistant.domain.repository

import com.example.healthassistant.domain.model.news.NewsArticle

interface NewsRepository {
    suspend fun getHealthNews(): List<NewsArticle>
}
