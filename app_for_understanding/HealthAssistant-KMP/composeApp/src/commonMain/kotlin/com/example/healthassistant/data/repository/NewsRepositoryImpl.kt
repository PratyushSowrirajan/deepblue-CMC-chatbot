package com.example.healthassistant.data.repository

import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.core.utils.RelativeTimeFormatter
import com.example.healthassistant.data.remote.news.NewsApi
import com.example.healthassistant.domain.model.news.NewsArticle
import com.example.healthassistant.domain.repository.NewsRepository

class NewsRepositoryImpl(
    private val api: NewsApi
) : NewsRepository {

    private var cachedArticles: List<NewsArticle>? = null

    override suspend fun getHealthNews(): List<NewsArticle> {

        // 🔹 Return cached data if already loaded
        cachedArticles?.let {
            AppLogger.d("NEWS_REPO", "Returning cached news")
            return it
        }

        AppLogger.d("NEWS_REPO", "Fetching strict health news (page 1 only)")

        val response = api.fetchHealthNews()

        if (response.status == "error") {
            AppLogger.d("NEWS_REPO", "API ERROR: ${response.message}")
            return emptyList()
        }

        val rawArticles = response.articles ?: emptyList()

        AppLogger.d("NEWS_REPO", "Fetched ${rawArticles.size} raw articles")

        // 🔹 Remove duplicate titles
        val unique = rawArticles.distinctBy { it.title }

        // 🔹 Strict health filtering
        val filtered = unique.filter { article ->

            val text = (article.title.orEmpty() + " " + article.description.orEmpty())
                .lowercase()

            val positiveMatch =
                text.contains("disease") ||
                        text.contains("virus") ||
                        text.contains("infection") ||
                        text.contains("outbreak") ||
                        text.contains("epidemic") ||
                        text.contains("pandemic") ||
                        text.contains("health alert") ||
                        text.contains("who") ||
                        text.contains("medical")

            val negativeMatch =
                text.contains("sports") ||
                        text.contains("cricket") ||
                        text.contains("football") ||
                        text.contains("movie") ||
                        text.contains("celebrity") ||
                        text.contains("crypto") ||
                        text.contains("election") ||
                        text.contains("minister") ||
                        text.contains("policy") ||
                        text.contains("tech") ||
                        text.contains("ai") ||
                        text.contains("smartphone")

            val hasValidImage =
                !article.urlToImage.isNullOrBlank() &&
                        article.urlToImage.startsWith("http")

            positiveMatch && !negativeMatch && hasValidImage
        }

        AppLogger.d("NEWS_REPO", "After strict filtering: ${filtered.size} articles")

        val resultList = filtered.map {
            NewsArticle(
                title = it.title ?: "",
                description = it.description ?: "No description available",
                imageUrl = it.urlToImage,
                publishedTime = RelativeTimeFormatter.format(it.publishedAt),
                sourceName = it.source?.name ?: "Unknown"
            )
        }

        // 🔹 Save to cache
        cachedArticles = resultList

        return resultList
    }



    private fun formatTime(date: String?): String {
        return "Recently"
    }
}

