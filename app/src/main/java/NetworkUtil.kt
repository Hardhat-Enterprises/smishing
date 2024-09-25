package com.example.smishingdetectionapp

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.HttpUrl
import java.io.IOException
import java.util.concurrent.TimeUnit

object NetworkUtil {
    @Throws(IOException::class)
    fun makeSecureRequest(url: String?): String {
        // Check for null URL and throw an appropriate exception
        val actualUrl = url ?: throw IllegalArgumentException("URL cannot be null")

        // Log the original URL
        Log.d("NetworkUtil", "Original URL: $actualUrl")

        // Enforce HTTPS and attempt to convert HTTP to HTTPS automatically
        val secureUrl = if (actualUrl.startsWith("http://")) {
            actualUrl.replace("http://", "https://").also {
                Log.w("NetworkUtil", "Insecure URL detected. Auto-converted to HTTPS: $it")
            }
        } else if (actualUrl.startsWith("https://")) {
            actualUrl
        } else {
            throw IOException("Invalid URL scheme: $actualUrl. HTTPS is required.")
        }

        // Create an OkHttpClient with timeouts
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        // Build the request
        val request: Request = Request.Builder()
            .url(secureUrl)
            .build()

        // Execute the request and handle response
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                // Log and throw an error for non-2xx responses
                Log.e("NetworkUtil", "Request failed with HTTP code: ${response.code}")
                throw IOException("Unexpected HTTP code $response")
            }

            // Retrieve the response body
            val responseBody = response.body?.string() ?: throw IOException("Response body is null")

            // Log the response body for debugging
            Log.d("NetworkUtil", "Response received: $responseBody")

            return responseBody
        }
    }
}
