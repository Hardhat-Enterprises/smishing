package com.example.smishingdetectionapp.model;

import com.google.gson.annotations.SerializedName;

public class ReportRequest {
    @SerializedName("phoneNumber")
    private final String phoneNumber;

    // Backend requires this exact key:
    @SerializedName("messageText")
    private final String messageText;

    public ReportRequest(String phoneNumber, String messageText) {
        this.phoneNumber = phoneNumber;
        this.messageText = messageText;
    }
}
