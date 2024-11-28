package com.example.smishingdetectionapp.chat;

public class ChatMessage {
    public static final int USER_MESSAGE = 0;
    public static final int BOT_MESSAGE = 1;

    private String message;
    private int messageType;

    public ChatMessage(String message, int messageType) {
        this.message = message;
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public int getMessageType() {
        return messageType;
    }
}