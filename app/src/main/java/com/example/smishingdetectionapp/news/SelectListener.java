package com.example.smishingdetectionapp.news;

import com.example.smishingdetectionapp.news.models.NewsArticle;

/**
 * Interface for handling click events on news articles.
 * Updated to work with REST API models instead of RSS
 */
public interface SelectListener {
    /**
     * Called when a news article is clicked
     * @param article The NewsArticle that was clicked
     */
    void OnNewsClicked(NewsArticle article);
}


