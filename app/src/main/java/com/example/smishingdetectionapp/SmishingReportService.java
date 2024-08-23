package com.example.smishingdetectionapp;

import java.util.ArrayList;
import java.util.List;

public class SmishingReportService {

    private List<String> reportedMessages;
    private List<String> falsePositives;
    private List<String> falseNegatives;

    public SmishingReportService() {
        this.reportedMessages = new ArrayList<>();
        this.falsePositives = new ArrayList<>();
        this.falseNegatives = new ArrayList<>();
    }

    // Smishing Message Reporting Method
    public void reportSmishing(String message, boolean isFalsePositive, boolean isFalseNegative) {
        reportedMessages.add(message);

        if (isFalsePositive) {
            falsePositives.add(message);
        }

        if (isFalseNegative) {
            falseNegatives.add(message);
        }

        //
    }

    // False Positive list lookup
    public List<String> getFalsePositives() {
        return falsePositives;
    }

    // False Negative list lookup
    public List<String> getFalseNegatives() {
        return falseNegatives;
    }

    // All reported Messages lookup
    public List<String> getAllReportedMessages() {
        return reportedMessages;
    }
}

