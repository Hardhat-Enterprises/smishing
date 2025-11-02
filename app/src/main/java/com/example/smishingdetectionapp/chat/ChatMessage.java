package com.example.smishingdetectionapp.chat;

/**
 * Represents a chat message in the conversation between user and bot.
 */
public class ChatMessage {

    // Constants to identify message sender type
    public static final int USER = 0;
    public static final int BOT = 1;

    // The content of the message
    private String message;

    // Type of message (USER or BOT)
    private int messageType;

    // Timestamp of the message (in millis)
    private long timestamp;

    /**
     * Constructs a ChatMessage with specified content, sender type, and timestamp.
     *
     * @param message The message content.
     * @param messageType The sender type (USER or BOT).
     * @param timestamp The time the message was created (System.currentTimeMillis()).
     */
    public ChatMessage(String message, int messageType, long timestamp) {
        this.message = message;
        this.messageType = messageType;
        this.timestamp = timestamp;
    }

    /**
     * Convenience constructor:
     * Constructs a ChatMessage with current system time as timestamp.
     *
     * @param message The message content.
     * @param messageType The sender type (USER or BOT).
     */
    public ChatMessage(String message, int messageType) {
        this.message = message;
        this.messageType = messageType;
        this.timestamp = System.currentTimeMillis(); // auto timestamp
    }

    /**
     * Gets the message content.
     *
     * @return The message text.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the message type.
     *
     * @return 0 if sent by USER, 1 if sent by BOT.
     */
    public int getMessageType() {
        return messageType;
    }

    /**
     * Gets the timestamp of the message.
     *
     * @return The time in milliseconds when the message was created.
     */
    public long getTimestamp() {
        return timestamp;
    }
}
