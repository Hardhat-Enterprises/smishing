package com.example.smishingdetectionapp.data.model;

public class SMSMessage {
    private String sender;
    private String body;

    public SMSMessage(String sender, String body) {
        this.sender = sender;
        this.body = body;
    }

    public String getSender() {
        return sender;
    }

    public String getBody() {
        return body;
    }
}
