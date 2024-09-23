package com.example.smishingdetectionapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class UrlSafetyCheckerActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_url_safety_checker)

        val urlInput = findViewById<EditText>(R.id.url_input)
        val checkButton = findViewById<Button>(R.id.check_url_button)

        checkButton.setOnClickListener {
            val url = urlInput.text.toString()
            if (url.isNotEmpty()) {
                checkUrlSafety(url)
            } else {
                Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUrlSafety(url: String) {
        val jsonBody = JSONObject().apply {
            put("client", JSONObject().apply {
                put("clientId", "com.example.smishingdetectionapp")
                put("clientVersion", "1.0")
            })
            put("threatInfo", JSONObject().apply {
                put("threatTypes", JSONArray(listOf("MALWARE", "SOCIAL_ENGINEERING")))
                put("platformTypes", JSONArray(listOf("ANY_PLATFORM")))
                put("threatEntryTypes", JSONArray(listOf("URL")))
                put("threatEntries", JSONArray().apply {
                    put(JSONObject().apply {
                        put("url", url)
                    })
                })
            })
        }

        Log.d("UrlSafetyChecker", "Constructed JSON Body: $jsonBody")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestBody = jsonBody.toString()
                    .toRequestBody("application/json".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url("https://safebrowsing.googleapis.com/v4/threatMatches:find?key=${BuildConfig.SAFE_BROWSING_API_KEY}")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                withContext(Dispatchers.Main) {
                    if (!response.isSuccessful) {
                        Log.e("UrlSafetyChecker", "Request failed: ${response.code} - ${response.message}")
                        Toast.makeText(this@UrlSafetyCheckerActivity, "Error: ${response.message}", Toast.LENGTH_LONG).show()
                    } else {
                        val responseBody = response.body?.string()
                        Log.d("UrlSafetyChecker", "API Response: $responseBody")

                        if (responseBody.isNullOrEmpty() || responseBody == "{}") {
                            Toast.makeText(this@UrlSafetyCheckerActivity, "URL is safe!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@UrlSafetyCheckerActivity, "Warning! This URL may be unsafe.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("UrlSafetyChecker", "Error during URL safety check", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UrlSafetyCheckerActivity, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
