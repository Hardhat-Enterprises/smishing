package com.example.smishingdetectionapp.chat;

import android.os.Bundle;
import android.util.Log; 
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smishingdetectionapp.R;

public class ChatAssistantActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;

@Override
protected void onCreate(Bundle savedInstanceState) {
    try {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_assistant);
        
        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        
    } catch (Exception e) {
        Log.e("ChatAssistantActivity", "Error in onCreate: " + e.getMessage());
        Toast.makeText(this, "Error initializing chat", Toast.LENGTH_SHORT).show();
        finish();
    }
}

private void initializeViews() {
    try {
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        
        if (chatRecyclerView == null || messageInput == null || sendButton == null) {
            throw new IllegalStateException("Required views not found in layout");
        }
    } catch (Exception e) {
        Log.e("ChatAssistantActivity", "Error initializing views: " + e.getMessage());
        throw e;
    }
}

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter();
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        ImageButton backButton = findViewById(R.id.chat_back);
        backButton.setOnClickListener(v -> finish());

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            chatAdapter.addMessage(new ChatMessage(message, ChatMessage.USER_MESSAGE));
            messageInput.setText("");
            // TODO: Implement chat bot response
        }
    }
}