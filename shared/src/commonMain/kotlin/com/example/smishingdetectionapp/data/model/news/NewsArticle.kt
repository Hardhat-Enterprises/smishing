package com.example.smishingdetectionapp.data.model.news

data class NewsArticle(
    val title: String,
    val link: String,
    val description: String? = null,
    val publishedDate: String? = null,
    val isBookmarked: Boolean = false
)