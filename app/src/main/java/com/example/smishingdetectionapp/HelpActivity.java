package com.example.smishingdetectionapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.smishingdetectionapp.Community.CommunityReportActivity;
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
        ImageButton report_back = findViewById(R.id.report_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // Contact Us
        RelativeLayout rv2 = findViewById(R.id.rv_2);
        rv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:+1234567890"));
                startActivity(phoneIntent);
            }
        });

        // Mail Us
        RelativeLayout rv1 = findViewById(R.id.rv_1);
        rv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:support@example.com"));
                startActivity(emailIntent);
            }
        });

        // FAQ
        RelativeLayout rv3 = findViewById(R.id.rv_3);
        rv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HelpActivity.this, "Faq", Toast.LENGTH_SHORT).show();
            }
        });

        // Feedback - navigate to FeedbackActivity
        RelativeLayout rv4 = findViewById(R.id.rv_4);
        rv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HelpActivity.this, FeedbackActivity.class));
                finish();
            }
        });
    }
}
