package com.example.smishingdetectionapp;

public class Report {
    private int userId;
    private String message;
    private boolean isSmishing;
    private String feedback;

    public Report(int userId, String message, boolean isSmishing, String feedback) {
        this.userId = userId;
        this.message = message;
        this.isSmishing = isSmishing;
        this.feedback = feedback;
    }

}
