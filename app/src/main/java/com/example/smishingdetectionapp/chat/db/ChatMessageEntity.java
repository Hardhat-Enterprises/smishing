package com.example.smishingdetectionapp.chat.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ChatMessageEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String sender;

    @NonNull
    public String text;

    public long timestamp;

    // constructor
    public ChatMessageEntity(@NonNull String sender,
                             @NonNull String text,
                             long timestamp) {
        this.sender = sender;
        this.text   = text;
        this.timestamp = timestamp;
    }
}
