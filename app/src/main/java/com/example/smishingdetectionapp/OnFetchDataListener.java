package com.example.smishingdetectionapp;

import com.example.smishingdetectionapp.Models.NewsHeadlines;

import java.util.List;

public interface OnFetchDataListener<NewsAPIResponse> {
    void onFetchData(List<NewsHeadlines> list, String message);
    void onError(String message);
}
