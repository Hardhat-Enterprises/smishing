package com.example.smishingdetectionapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.ui.FaqActivity;
import com.example.smishingdetectionapp.navigation.BottomNavCoordinator;
import com.google.android.material.card.MaterialCardView;

public class HelpActivity extends SharedActivity {

    public static final String EXTRA_FAQ_KEY = "faq_key";

    private void openFaq(String key) {
        Intent i = new Intent(this, FaqActivity.class);
        if (key != null) i.putExtra(EXTRA_FAQ_KEY, key);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_updated);

        BottomNavCoordinator.setup(this, R.id.nav_settings);

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

        // Call Us
        MaterialCardView cardCallUs = findViewById(R.id.cardCallUs);
        cardCallUs.setOnClickListener(v -> {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            phoneIntent.setData(Uri.parse("tel:+1234567890"));
            startActivity(phoneIntent);
        });

        // Mail Us
        MaterialCardView cardMailUs = findViewById(R.id.cardMailUs);
        cardMailUs.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@example.com"));
            startActivity(emailIntent);
        });

        // FAQ
        MaterialCardView cardFAQ = findViewById(R.id.cardFAQ);
        cardFAQ.setOnClickListener(v -> openFaq(null));

        // Feedback
        MaterialCardView cardFeedback = findViewById(R.id.cardFeedback);
        cardFeedback.setOnClickListener(v -> {
            startActivity(new Intent(this, FeedbackActivity.class));
            finish();
        });

        // Common topic cards
        MaterialCardView cardTopic1 = findViewById(R.id.cardTopic1);
        cardTopic1.setOnClickListener(v -> openFaq("detect"));

        MaterialCardView cardTopic2 = findViewById(R.id.cardTopic2);
        cardTopic2.setOnClickListener(v -> openFaq("report"));

        MaterialCardView cardTopic3 = findViewById(R.id.cardTopic3);
        cardTopic3.setOnClickListener(v -> openFaq("phishing"));
    }
}