package com.example.smishingdetectionapp.net

import com.example.smishingdetectionapp.net.models.ReportRequest
import com.example.smishingdetectionapp.net.models.ReportResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/reports")
    suspend fun postReport(@Body body: ReportRequest): Response<ReportResponse>
}
