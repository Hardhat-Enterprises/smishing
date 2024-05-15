package com.example.smishingdetectionapp.data.model;

public class Message {
    private String sender;
    private String body;

    public Message(String sender, String body) {
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
