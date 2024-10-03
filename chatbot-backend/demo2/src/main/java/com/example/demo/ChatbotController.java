package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    private final SmishingService smishingService;

    public ChatbotController(SmishingService smishingService) {
        this.smishingService = smishingService;
    }

    @PostMapping("/ask")
    public ResponseEntity<String> handleSmishingQuestion(@RequestBody String question) {
        try {
            String response = smishingService.processSmishingAwarenessQuestion(question);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleException(e, "Error processing smishing awareness question.");
        }
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> handleMessageAnalysis(@RequestBody String message) {
        try {
            String result = smishingService.analyzeMessageForFraud(message);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return handleException(e, "Error analyzing the message for fraud.");
        }
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<String> handleException(Exception e, String customMessage) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(customMessage + " " + e.getMessage());
    }
}
