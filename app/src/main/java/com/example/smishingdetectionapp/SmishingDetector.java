package com.example.smishingdetectionapp;

import java.util.Arrays;
import java.util.List;

public class SmishingDetector {
    private static final List<String> SMISHING_KEYWORDS = Arrays.asList(
            "win", "congratulations", "free", "claim", "urgent", "click", "prize", "won", "gift", "cash"
    );

    public static boolean isSmishingMessage(String message) {
        for (String keyword : SMISHING_KEYWORDS) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
