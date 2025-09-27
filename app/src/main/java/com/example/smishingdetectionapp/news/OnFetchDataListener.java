package com.example.smishingdetectionapp.news;

import com.example.smishingdetectionapp.news.models.NewsArticle;
import java.util.List;

/**
 * Interface for handling news data fetch callbacks
 * Updated to work with REST API models instead of RSS
 */
public interface OnFetchDataListener {
    
    /**
     * Method to be called when news data is successfully fetched.
     * @param articles List of NewsArticle objects representing the fetched news
     * @param message Success message from the API
     */
    void onFetchData(List<NewsArticle> articles, String message);
    
    /**
     * Method to be called when there is an error during the data fetching process.
     * @param message Error message describing what went wrong
     */
    void onError(String message);
}
