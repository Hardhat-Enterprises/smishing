package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ActivityFeedback extends AppCompatActivity {
    private ImageView emojiAngry, emojiSad, emojiNeutral, emojiHappy, emojiExcited;
    private EditText inputFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize views
        ImageButton backButton = findViewById(R.id.report_back);
        emojiAngry = findViewById(R.id.emojiAngry);
        emojiSad = findViewById(R.id.emojiSad);
        emojiNeutral = findViewById(R.id.emojiNeutral);
        emojiHappy = findViewById(R.id.emojiHappy);
        emojiExcited = findViewById(R.id.emojiExcited);
        inputFeedback = findViewById(R.id.inputFeedback);
        View btnSubmit = findViewById(R.id.btnSubmit);

        // Back button functionality
        backButton.setOnClickListener(v -> finish());

        // Pass all emoji views as an array to the hover effect method
        ImageView[] emojis = {emojiAngry, emojiSad, emojiNeutral, emojiHappy, emojiExcited};
        setEmojiHoverEffect(emojis);

        // Submit button functionality
        btnSubmit.setOnClickListener(v -> {
            String feedbackText = inputFeedback.getText().toString().trim();
            if (!feedbackText.isEmpty()) {
                // Handle feedback submission (e.g., send to server or save locally)
                Toast.makeText(ActivityFeedback.this, "Feedback submitted! Thank you.", Toast.LENGTH_SHORT).show();
                inputFeedback.setText(""); // Clear the input field
            } else {
                Toast.makeText(ActivityFeedback.this, "Please enter your feedback.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEmojiHoverEffect(ImageView[] emojis) {
        // Variable to track the currently selected emoji
        final ImageView[] selectedEmoji = {null};

        for (ImageView emoji : emojis) {
            emoji.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (selectedEmoji[0] != emoji) {
                            // Change the emoji color to hover
                            emoji.setImageTintList(ContextCompat.getColorStateList(v.getContext(), R.color.emoji_hover));
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (selectedEmoji[0] != emoji) {
                            // Deselect the previously selected emoji
                            if (selectedEmoji[0] != null) {
                                selectedEmoji[0].setImageTintList(ContextCompat.getColorStateList(v.getContext(), R.color.emoji_normal));
                            }

                            // Set the current emoji to the selected state
                            emoji.setImageTintList(ContextCompat.getColorStateList(v.getContext(), R.color.emoji_selected));
                            selectedEmoji[0] = emoji; // Update the selected emoji reference
                        } else {
                            // If the same emoji is clicked, deselect it
                            emoji.setImageTintList(ContextCompat.getColorStateList(v.getContext(), R.color.emoji_normal));
                            selectedEmoji[0] = null;
                        }
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        if (selectedEmoji[0] != emoji) {
                            // Revert to normal state if not selected
                            emoji.setImageTintList(ContextCompat.getColorStateList(v.getContext(), R.color.emoji_normal));
                        }
                        break;
                }
                return true;
            });

            emoji.setOnClickListener(v -> {
                // Handle click behavior (optional toast for selected emoji)
                if (selectedEmoji[0] == emoji) {
                    Toast.makeText(v.getContext(), "Emoji already selected!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Emoji selected!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
