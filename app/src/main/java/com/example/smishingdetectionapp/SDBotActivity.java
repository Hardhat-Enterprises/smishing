package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SDBotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sd_bot);

        // Initialize the back button to go back to the previous screen
        ImageButton backButton = findViewById(R.id.sdBot_back_button);
        backButton.setOnClickListener(v -> {
            // Navigate back to the previous screen (replace with the correct activity)
            finish();
        });

        // Initialize chat layout, user input, and send button
        final LinearLayout chatLayout = findViewById(R.id.chatLayout);
        final EditText userInput = findViewById(R.id.userInput);
        final ImageButton sendMessageButton = findViewById(R.id.sendMessageButton);

        // Initially disable send button
        sendMessageButton.setEnabled(false);

        // TextWatcher to enable the send button only when the input field is filled
        userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable the send button only if the user has entered text
                sendMessageButton.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Send button functionality to send user message and simulate bot response
        sendMessageButton.setOnClickListener(v -> {
            String userMessage = userInput.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                // Add the user's message to the chat layout
                addMessageToChat(chatLayout, userMessage, true);

                // Clear input field
                userInput.setText("");

                // Simulate bot reply after a short delay
                new Handler().postDelayed(() -> {
                    String botReply = generateBotResponse(userMessage);
                    addMessageToChat(chatLayout, botReply, false);
                }, 1000); // Simulate 1-second delay
            }
        });
    }

    // Method to add a message to the chat layout (from user or bot)
    private void addMessageToChat(LinearLayout chatLayout, String message, boolean isUser) {
        TextView messageView = new TextView(this);

        // Style the message based on whether it's from the user or bot
        messageView.setText(message);
        messageView.setPadding(16, 16, 16, 16);
        messageView.setTextSize(16);

        if (isUser) {
            messageView.setBackgroundColor(ContextCompat.getColor(this, R.color.baby_blue));;
            messageView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END); // Align user messages to the right
        } else {
            messageView.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_green));
            messageView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START); // Align bot messages to the left
        }

        // Add the message to the chat layout
        chatLayout.addView(messageView);

        // Optionally, you can add scrolling behavior here if the chat grows longer
    }

    // Method to simulate bot responses based on user input (placeholder logic)
    private String generateBotResponse(String userMessage) {
        // Simulated bot logic: Replace this with actual chatbot logic
        if (userMessage.toLowerCase().contains("hello")) {
            return "Hi! How can I assist you today?";
        } else if (userMessage.toLowerCase().contains("help")) {
            return "I am here to help you with any issues you may have.";
        } else {
            return "I'm sorry, I didn't understand that. Could you please rephrase?";
        }
    }
}
