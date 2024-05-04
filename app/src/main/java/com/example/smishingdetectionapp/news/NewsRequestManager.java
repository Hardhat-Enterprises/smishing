package com.example.smishingdetectionapp.news;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smishingdetectionapp.news.Models.NewsAPIResponse;
import com.example.smishingdetectionapp.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

// NewsRequestManager class to handle API requests for fetching news headlines
public class NewsRequestManager {
    Context context;

    // Retrofit instance setup to make network requests
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    // Method to fetch news headlines using the News API
    public void getNewsHeadlines(OnFetchDataListener<NewsAPIResponse> listener, String category, String q){
        CallNewsApi callNewsApi = retrofit.create(CallNewsApi.class);
        Call<NewsAPIResponse> call = callNewsApi.callHeadlines("au", category, q,context.getString(R.string.api_key));

        try {
            // Enqueue the call to be executed asynchronously
            call.enqueue(new Callback<NewsAPIResponse>() {
                @Override
                public void onResponse(@NonNull Call<NewsAPIResponse> call, @NonNull Response<NewsAPIResponse> response) {
                    if (!response.isSuccessful()){
                        Toast.makeText(context, "Error!!", Toast.LENGTH_SHORT).show();
                    }

                    assert response.body() != null;
                    listener.onFetchData(response.body().getArticles(), response.message()); // Passing the fetched data to the listener
                }

                @Override
                public void onFailure(@NonNull Call<NewsAPIResponse> call, @NonNull Throwable throwable) {
                    listener.onError("Request Failed!");
                }
            });
        }
        catch (Exception e){
            e.printStackTrace(); // Print the stack trace if an exception occurs
        }
    }

    // Constructor to initialize the NewsRequestManager with a context
    public NewsRequestManager(Context context) {this.context = context;}

    // Interface to define the endpoints for the News API
    public interface CallNewsApi {
        @GET("top-headlines") // Annotation for GET request for top headlines
        Call<NewsAPIResponse> callHeadlines(
            @Query("country") String country,
            @Query("category") String category,
            @Query("q") String q,
            @Query("apiKey") String api_key
        );
    }
}
