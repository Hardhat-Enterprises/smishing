package com.example.smishingdetectionapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/submitReport")
    Call<ReportResponse> submitReport(@Body Report report);

    @POST("/submitFeedback")
    Call<ReportResponse> submitFeedback(@Body Report report);
}

