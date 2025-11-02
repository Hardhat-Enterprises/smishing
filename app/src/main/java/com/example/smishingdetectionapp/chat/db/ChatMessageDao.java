package com.example.smishingdetectionapp.chat.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ChatMessageDao {

    @Insert
    void insert(ChatMessageEntity entity);

    // Order messages by timestamp (oldest to newest)
    @Query("SELECT * FROM ChatMessageEntity ORDER BY timestamp ASC")
    List<ChatMessageEntity> getAll();

    @Query("DELETE FROM ChatMessageEntity")
    void deleteAll();
}
