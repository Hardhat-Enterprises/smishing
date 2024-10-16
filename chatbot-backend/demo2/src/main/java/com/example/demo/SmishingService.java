package com.example.demo;

import org.springframework.stereotype.Service;

@Service
public class SmishingService {

    private final GeminiService geminiService;
    private final TensorFlowService tensorFlowService;

    public SmishingService(GeminiService geminiService, TensorFlowService tensorFlowService) {
        this.geminiService = geminiService;
        this.tensorFlowService = tensorFlowService;
    }

    public String processSmishingAwarenessQuestion(String question) {
        try {
            // Calls Google Gemini NLU service
            return geminiService.sendToGeminiNLU(question);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process smishing awareness question.", e);
        }
    }

    public String analyzeMessageForFraud(String message) {
        try {
            // Use TensorFlow model or Google Gemini for message fraud detection
            return tensorFlowService.analyzeMessage(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze the message for fraud.", e);
        }
    }
}
