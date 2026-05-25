package com.example.smishingdetectionapp.data.repository

import com.example.smishingdetectionapp.data.model.news.NewsArticle

// Repository interface for news fetching and saved article operations
interface NewsRepository {

    // Latest news articles fetched
    suspend fun fetchLatestNews(): List<NewsArticle>

    //Get all saved news articles
    suspend fun getSavedNews(): List<NewsArticle>

    // Save news article
    suspend fun saveNewsArticle(article: NewsArticle)

    // Remove saved news article by link
    suspend fun removeSavedNewsArticle(link: String)

    // Checks whether news article is already saved
    suspend fun isNewsArticleSaved(link: String): Boolean
}