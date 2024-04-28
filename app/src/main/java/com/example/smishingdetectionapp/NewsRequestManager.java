package com.example.smishingdetectionapp;

import android.content.Context;
import android.location.GnssAntennaInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smishingdetectionapp.Models.NewsAPIResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class NewsRequestManager {
    Context context;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public void getNewsHeadlines(OnFetchDataListener<NewsAPIResponse> listener, String category, String q){
        CallNewsApi callNewsApi = retrofit.create(CallNewsApi.class);
        Call<NewsAPIResponse> call = callNewsApi.callHeadlines("au", category, q,context.getString(R.string.api_key));

        try {
            call.enqueue(new Callback<NewsAPIResponse>() {
                @Override
                public void onResponse(@NonNull Call<NewsAPIResponse> call, @NonNull Response<NewsAPIResponse> response) {
                    if (!response.isSuccessful()){
                        Toast.makeText(context, "Error!!", Toast.LENGTH_SHORT).show();
                    }

                    assert response.body() != null;
                    listener.onFetchData(response.body().getArticles(), response.message());
                }

                @Override
                public void onFailure(@NonNull Call<NewsAPIResponse> call, @NonNull Throwable throwable) {
                    listener.onError("Request Failed!");
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public NewsRequestManager(Context context) {this.context = context;}

    public interface CallNewsApi {
        @GET("top-headlines")
        Call<NewsAPIResponse> callHeadlines(
            @Query("country") String country,
            @Query("category") String category,
            @Query("q") String q,
            @Query("apiKey") String api_key
        );
    }
}
