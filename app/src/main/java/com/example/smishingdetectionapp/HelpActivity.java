package com.example.smishingdetectionapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.navigation.BottomNavCoordinator;
import com.example.smishingdetectionapp.ui.FaqActivity;
import com.google.android.material.card.MaterialCardView;

public class HelpActivity extends SharedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_updated);

        BottomNavCoordinator.setup(this, R.id.bottom_navigation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button
        ImageButton helpBack = findViewById(R.id.help_back);
        helpBack.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // Menu button
        ImageButton helpMenu = findViewById(R.id.help_menu);
        helpMenu.setOnClickListener(v -> {
            // Implement menu actions if necessary
        });

        // Common Topics
        MaterialCardView cardTopic1 = findViewById(R.id.cardTopic1);
        cardTopic1.setOnClickListener(v -> {
            Intent intent = new Intent(this, TopicDetailActivity.class);
            intent.putExtra("TOPIC_ID", "DETECT_SMISHING");
            startActivity(intent);
        });

        MaterialCardView cardTopic2 = findViewById(R.id.cardTopic2);
        cardTopic2.setOnClickListener(v -> {
            Intent intent = new Intent(this, TopicDetailActivity.class);
            intent.putExtra("TOPIC_ID", "REPORT_SMS");
            startActivity(intent);
        });

        MaterialCardView cardTopic3 = findViewById(R.id.cardTopic3);
        cardTopic3.setOnClickListener(v -> {
            Intent intent = new Intent(this, TopicDetailActivity.class);
            intent.putExtra("TOPIC_ID", "SMISHING_VS_PHISHING");
            startActivity(intent);
        });

        // FAQ
        MaterialCardView cardFAQ = findViewById(R.id.cardFAQ);
        if (cardFAQ != null) cardFAQ.setOnClickListener(v -> openFaq(null));

        // Call Us
        MaterialCardView cardCallUs = findViewById(R.id.cardCallUs);
        if (cardCallUs != null) {
            cardCallUs.setOnClickListener(v -> {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:+1234567890"));
                startActivity(phoneIntent);
            });
        }

        // Mail Us
        MaterialCardView cardMailUs = findViewById(R.id.cardMailUs);
        if (cardMailUs != null) {
            cardMailUs.setOnClickListener(v -> {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:support@example.com"));
                startActivity(emailIntent);
            });
        }

        // Feedback
        MaterialCardView cardFeedback = findViewById(R.id.cardFeedback);
        if (cardFeedback != null) {
            cardFeedback.setOnClickListener(v ->
                    startActivity(new Intent(HelpActivity.this, FeedbackActivity.class))
            );
        }
    }

    // ── Opens the FAQ screen, optionally scrolling to a specific key ──
    private void openFaq(String key) {
        Intent intent = new Intent(this, FaqActivity.class);
        if (key != null) {
            intent.putExtra("FAQ_KEY", key);
        }
        startActivity(intent);
    }
}