package com.example.smishingdetectionapp;

public class MockThreatRepository implements ThreatRepository {

    @Override
    public int calculateThreatScore() {
        // Mock data - simulating real threat detection
        int newDetections24h = 1;  // 1 new SMS threat in last 24h
        int highSeverity7d = 1;    // 1 high severity event in last 7d

        // Scoring formula: new24h × 25 + high7d × 10
        return (newDetections24h * 25) + (highSeverity7d * 10);
    }

    @Override
    public int getThreatLevel() {
        int score = calculateThreatScore();

        // Thresholds from mockup: ≥70 ALERT, ≥30 CAUTION, else SAFE
        if (score >= 70) {
            return 2; // ALERT
        } else if (score >= 30) {
            return 1; // CAUTION
        } else {
            return 0; // SAFE
        }
    }
}