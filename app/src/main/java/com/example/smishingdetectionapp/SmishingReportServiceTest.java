package com.example.smishingdetectionapp;


import android.util.Log;

public class SmishingReportServiceTest {

    public static void runTests() {
        SmishingReportService service = new SmishingReportService();

        // Test 1: Report a False Positive message
        service.reportSmishing("Test smishing message", true, false);
        Log.d("SmishingTest", "False Positives Count: " + service.getFalsePositives().size());
        Log.d("SmishingTest", "First False Positive Message: " + service.getFalsePositives().get(0));

        // Test 2: Report a False Negative message
        service.reportSmishing("Another message", false, true);
        Log.d("SmishingTest", "False Negatives Count: " + service.getFalseNegatives().size());
        Log.d("SmishingTest", "First False Negative Message: " + service.getFalseNegatives().get(0));

        // Test 3: Check all reported messages
        Log.d("SmishingTest", "Total Reported Messages: " + service.getAllReportedMessages().size());
    }
}