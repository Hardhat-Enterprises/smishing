package com.example.smishingdetectionapp.news;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Context;

import com.example.smishingdetectionapp.news.Models.RSSFeedModel;

public class NewsRequestManager {
    Context context;

    public NewsRequestManager(Context context) {
        this.context = context;
    }

    // Fetches RSS feed from a specified site using Retrofit and notifies the listener.
    public void fetchRSSFeed(OnFetchDataListener<RSSFeedModel.Feed> listener) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.scamwatch.gov.au/rss/news-feed.xml/")// Example base UR// L
                .client(client)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        // Create an instance of the RSSApi interface
        RSSFeedModel.RSSApi rssApi = retrofit.create(RSSFeedModel.RSSApi.class);
        // Enqueue the request to fetch articles
        rssApi.getArticles("").enqueue(new Callback<RSSFeedModel.Feed>() {
            @Override
            public void onResponse(Call<RSSFeedModel.Feed> call, Response<RSSFeedModel.Feed> response) {
                // Check if the response is successful and contains the expected data
                if (response.isSuccessful() && response.body() != null) {
                    // Notify listener of successful data fetch
                    listener.onFetchData(response.body().channel.articles, "Success");
                } else {
                    // Notify listener of failure in fetching data
                    listener.onError("Failed to fetch data");
                }
            }

            @Override
            public void onFailure(Call<RSSFeedModel.Feed> call, Throwable t) {
                // Notify listener of an error during the network request
                listener.onError(t.getMessage());
            }
        });
    }
}
