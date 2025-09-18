package com.example.smishingdetectionapp.net

import android.util.Log
import com.example.smishingdetectionapp.net.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ReportRepository {
    suspend fun postReport(
        phoneNumber: String,
        messageText: String,
        url: String? = null,
        metadata: Map<String, String>? = null
    ): ReportResponse = withContext(Dispatchers.IO) {
        try {
            val req = ReportRequest(phoneNumber, messageText, url, "android", metadata)
            val resp = ApiClient.api.postReport(req)
            if (resp.isSuccessful) {
                resp.body() ?: fallback("empty-body")
            } else {
                Log.w("ReportRepository", "HTTP ${resp.code()} ${resp.message()}")
                fallback("http-${resp.code()}")
            }
        } catch (e: Exception) {
            Log.e("ReportRepository", "postReport error", e)
            fallback("exception: ${e.message}")
        }
    }

    private fun fallback(reason: String): ReportResponse {
        return ReportResponse(
            status = "error:$reason",
            classification = Classification(
                label = "ham",
                badge = "Safe",
                confidence = 0.05,
                severity = "low",
                advice = "Unable to reach server. Message treated as low risk. Stay cautious and verify unusual requests."
            ),
            analysis = Analysis(riskScore = 5, tags = emptyList())
        )
    }
}
