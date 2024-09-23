package com.example.demo;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.net.http.*;
import java.net.URI;
import java.io.IOException;

@Service
public class GeminiService {

    private static final Dotenv dotenv = Dotenv.load(); // Loads the .env file
    private static final String GEMINI_API_URL = "https://gemini.api.url";  // Replace with actual API
    private static final String GEMINI_API_KEY = dotenv.get("GEMINI_API_KEY");  // Load API Key from .env

    public String sendToGeminiNLU(String input) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_API_URL))
                .header("Authorization", "Bearer " + GEMINI_API_KEY)  // Use the API key in the header
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"input\":\"" + input + "\"}"))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new IOException("Gemini API responded with error: " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Error communicating with Google Gemini API", e);
        }
    }
}
