package com.example.smishingdetectionapp.chat;

import android.util.Log;
import java.io.IOException;
import okhttp3.*;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;

public class OllamaClient {
    private static final String BASE_URL = "https://17e5-112-134-224-5.ngrok-free.app";
    private final OkHttpClient client;

    public interface ResponseCallback {
        void onResponse(String response);
    }

    public OllamaClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void getResponse(String message, ResponseCallback callback) {
        new Thread(() -> {
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("model", "llama2");
                jsonBody.put("prompt", message);
                
                RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                    .url(BASE_URL + "/api/generate")
                    .post(body)
                    .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        String botResponse = jsonResponse.getString("response");
                        callback.onResponse(botResponse);
                    } else {
                        callback.onResponse("Sorry, I couldn't process your request.");
                    }
                }
            } catch (Exception e) {
                Log.e("OllamaClient", "Error getting response", e);
                callback.onResponse("Sorry, an error occurred.");
            }
        }).start();
    }
}