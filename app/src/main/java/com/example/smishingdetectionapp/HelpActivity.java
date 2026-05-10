package com.example.smishingdetectionapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.smishingdetectionapp.FeedbackActivity;
import com.example.smishingdetectionapp.ui.FaqActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.smishingdetectionapp.navigation.BottomNavCoordinator;
import com.google.android.material.card.MaterialCardView;


public class HelpActivity extends SharedActivity {
    // key used by FaqActivity to auto-expand the matching item
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

        // Adjust padding for system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button to go back to settings dashboard
        ImageButton report_back = findViewById(R.id.help_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // Contact Us
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

        // Feedback - navigate to FeedbackActivity
        MaterialCardView cardFeedback = findViewById(R.id.cardFeedback);
        cardFeedback.setOnClickListener(v -> {
            startActivity(new Intent(HelpActivity.this, FeedbackActivity.class));
            finish();
        });
    }
}
