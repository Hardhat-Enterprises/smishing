package com.example.smishingdetectionapp.network

import com.example.smishingdetectionapp.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val baseUrl = BuildConfig.SERVERIP // http://10.0.2.2:5000/

    val api: FaqApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FaqApiService::class.java)
    }
}