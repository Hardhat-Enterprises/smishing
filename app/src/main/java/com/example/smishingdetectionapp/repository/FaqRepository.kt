package com.example.smishingdetectionapp.repository

import com.example.smishingdetectionapp.models.FaqItem
import com.example.smishingdetectionapp.network.FaqApiService

class FaqRepository(private val api: FaqApiService) {
    suspend fun fetchFaqs(): List<FaqItem> = api.getFaqs()
}
