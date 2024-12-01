package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TermsAndConditionsActivity extends AppCompatActivity {

    private Button backButton;
    private ScrollView termsScrollView;
    private TextView termsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        // Initialize views
        backButton = findViewById(R.id.accept_button);
        termsScrollView = findViewById(R.id.terms_scroll_view);
        termsTextView = findViewById(R.id.terms_text_view);

        // Initially disable the back button
        backButton.setEnabled(false);

        // Enable the back button when user scrolls to the bottom of the terms
        termsScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            // Check if the ScrollView is scrolled to the bottom
            View contentView = termsScrollView.getChildAt(0);
            if (contentView != null &&
                    contentView.getBottom() <= (termsScrollView.getScrollY() + termsScrollView.getHeight())) {
                backButton.setEnabled(true); // Enable the back button
            } else {
                backButton.setEnabled(false); // Keep it disabled otherwise
            }
        });

        // Back button listener to finish the activity
        backButton.setOnClickListener(v -> finish());
    }
}
