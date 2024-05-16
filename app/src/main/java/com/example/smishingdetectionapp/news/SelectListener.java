package com.example.smishingdetectionapp.news;

import com.example.smishingdetectionapp.news.Models.RSSFeedModel;

// Interface for handling click events on news articles.
public interface SelectListener {
    void OnNewsClicked(RSSFeedModel.Article article);
}


