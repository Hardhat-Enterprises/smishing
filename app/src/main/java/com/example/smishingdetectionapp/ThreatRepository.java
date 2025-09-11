package com.example.smishingdetectionapp;

public interface ThreatRepository {
    int calculateThreatScore();
    int getThreatLevel(); // 0=SAFE, 1=CAUTION, 2=ALERT
}