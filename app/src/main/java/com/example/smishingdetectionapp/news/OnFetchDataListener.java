package com.example.smishingdetectionapp.news;

import com.example.smishingdetectionapp.news.Models.RSSFeedModel;

import java.util.List;

public interface OnFetchDataListener<NewsAPIResponse> {
    // Method to be called when data is successfully fetched.
    // This accepts a list of NewsHeadlines objects, which represent the fetched data,
    // and a message string, typically used for logging or displaying a status message.
    void onFetchData(List<RSSFeedModel.Article> list, String message);
    // Method to be called when there is an error during the data fetching process.
    // The method accepts a single string argument that describes the error, allowing for error handling,
    // such as logging the error or informing the user.
    void onError(String message);
}
