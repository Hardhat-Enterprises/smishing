package com.example.smishingdetectionapp.news.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model class representing the API response from the /api/news endpoint
 * Wraps the list of articles with pagination and status information
 */
public class NewsApiResponse {
    
    @SerializedName("success")
    public boolean success;
    
    @SerializedName("data")
    public List<NewsArticle> articles;
    
    @SerializedName("pagination")
    public PaginationInfo pagination;
    
    @SerializedName("message")
    public String message;
    
    /**
     * Checks if the response contains articles
     * @return true if articles list is not null and not empty
     */
    public boolean hasArticles() {
        return articles != null && !articles.isEmpty();
    }
    
    /**
     * Gets the number of articles in the response
     * @return Number of articles, or 0 if articles is null
     */
    public int getArticleCount() {
        return articles != null ? articles.size() : 0;
    }
    
    /**
     * Checks if there are more pages available
     * @return true if current page is less than total pages
     */
    public boolean hasMorePages() {
        return pagination != null && pagination.page < pagination.pages;
    }
    
    // Default constructor
    public NewsApiResponse() {}
    
    // Constructor for testing
    public NewsApiResponse(boolean success, List<NewsArticle> articles, String message) {
        this.success = success;
        this.articles = articles;
        this.message = message;
    }
}
