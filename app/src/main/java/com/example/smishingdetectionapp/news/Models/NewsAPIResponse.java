package com.example.smishingdetectionapp.news.Models;

import java.util.List;

// Define the NewsAPIResponse class which models the response structure from a news API
public class NewsAPIResponse {
    String status;
    int totalResults;
    List<NewsHeadlines> articles; // List to store the articles fetched from the API

    // Getter method to get the status of the API response
    public String getStatus() {
        return status;
    }

    // Setter method to set the status of the API response
    public void setStatus(String status) {
        this.status = status;
    }

    // Getter method to get the total number of results available
    public int getTotalResults() {
        return totalResults;
    }

    // Setter method to set the total number of results
    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    // Getter method to get the list of news articles
    public List<NewsHeadlines> getArticles() {
        return articles;
    }

    // Setter method to set the list of news articles
    public void setArticles(List<NewsHeadlines> articles) {
        this.articles = articles;
    }
}
