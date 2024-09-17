package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forums);

        // Initialize the back button to go back to the previous screen
        ImageButton report_back = findViewById(R.id.forum_back_button);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // Initializing input fields and submit buttons
        final EditText userThoughtsInput = findViewById(R.id.userThoughtsInput);
        final Button submitThoughtsButton = findViewById(R.id.submitThoughtsButton);
        final EditText commentInput = findViewById(R.id.commentInput);
        final Button submitCommentButton = findViewById(R.id.submitCommentButton);

        // Disabling submit buttons initially
        submitThoughtsButton.setEnabled(false);
        submitCommentButton.setEnabled(false);

        // TextWatcher to enable the submit thoughts button only when the input field is filled
        userThoughtsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable the submit button only if the user has entered text
                submitThoughtsButton.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // TextWatcher to enable the submit comment button only when the comment field is filled
        commentInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable the submit button only if the user has entered a comment
                submitCommentButton.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Submit thoughts button functionality
        submitThoughtsButton.setOnClickListener(v -> {
            String userThoughts = userThoughtsInput.getText().toString().trim();
            if (!userThoughts.isEmpty()) {
                // Simulate thoughts submission
                boolean isSubmitted = DatabaseAccess.submitThoughts(userThoughts);
                if (isSubmitted) {
                    userThoughtsInput.setText(null);
                    Toast.makeText(ForumActivity.this, "Thoughts submitted successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForumActivity.this, "Failed to submit thoughts. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Submit comment button functionality
        submitCommentButton.setOnClickListener(v -> {
            String comment = commentInput.getText().toString().trim();
            if (!comment.isEmpty()) {
                // Simulate comment submission
                boolean isSubmitted = DatabaseAccess.submitComment(comment);
                if (isSubmitted) {
                    commentInput.setText(null);
                    Toast.makeText(ForumActivity.this, "Comment submitted successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForumActivity.this, "Failed to submit comment. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Start a new post button (If you have functionality to handle new posts)
        Button startNewPostButton = findViewById(R.id.startNewPostButton);
        startNewPostButton.setOnClickListener(v -> {
            // Logic for starting a new post goes here (e.g., open a new activity or dialog)
            Toast.makeText(ForumActivity.this, "Starting a new post...", Toast.LENGTH_SHORT).show();
        });
    }
}

