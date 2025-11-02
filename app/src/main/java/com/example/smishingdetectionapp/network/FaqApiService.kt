package com.example.smishingdetectionapp.network

import com.example.smishingdetectionapp.models.FaqItem
import retrofit2.http.GET

interface FaqApiService {
    @GET("api/faq")
    suspend fun getFaqs(): List<FaqItem>
}
