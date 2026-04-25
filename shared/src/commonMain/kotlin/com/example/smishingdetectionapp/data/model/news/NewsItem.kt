package com.example.smishingdetectionapp.data.model.news

data class NewsItem(
    val title: String,
    val link: String,
    val description: String? = null,
    val pubDate: String? = null,
    val isBookmarked: Boolean = false
)
