package com.example.smishingdetectionapp.sms;

public interface OnStatsUpdateListener {

    void onStatsUpdated(int totalMessagesAnalyzed, int smishingMessagesCount);

}
