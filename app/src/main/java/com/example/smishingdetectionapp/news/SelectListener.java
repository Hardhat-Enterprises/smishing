package com.example.smishingdetectionapp.news;

import com.example.smishingdetectionapp.news.Models.RSSFeedModel;

public interface SelectListener {
    void OnNewsClicked(RSSFeedModel.Article article);
}

//public interface SelectListener {
    //void OnNewsClicked(NewsHeadlines headlines);

   // void onError(String message);
//}
