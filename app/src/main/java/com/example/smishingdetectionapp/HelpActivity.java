package com.example.smishingdetectionapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HelpActivity extends SharedActivity {

    private float initialY; // Variable to track the initial Y position for swipe detection
    private static final int SWIPE_THRESHOLD = 50; // Threshold for swipe detection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_updated);

        // Handle system window insets for immersive UI
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

        // Set OnTouchListener to detect swipe gestures
        findViewById(R.id.scroll_view).setOnTouchListener(this::onTouch);

        // Contact Us
        RelativeLayout rv2 = findViewById(R.id.rv_2);
        rv2.setOnClickListener(v -> {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            phoneIntent.setData(Uri.parse("tel:+1234567890")); // Replace with your phone number
            startActivity(phoneIntent);
        });

        // Mail Us
        RelativeLayout rv1 = findViewById(R.id.rv_1);
        rv1.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@example.com")); // Replace with your email
            startActivity(emailIntent);
        });

        // FAQ
        RelativeLayout rv3 = findViewById(R.id.rv_3);
        rv3.setOnClickListener(v -> {
            Intent intent = new Intent(this, FAQActivity.class); // Navigate to FAQActivity
            startActivity(intent);
        });

        // Feedback
        RelativeLayout rv4 = findViewById(R.id.rv_4);
        rv4.setOnClickListener(v -> {
            Intent feedbackIntent = new Intent(this, ActivityFeedback.class);
            startActivity(feedbackIntent);
        });

        // FAQ Button
        ImageView faqButton = findViewById(R.id.iv_faq);
        faqButton.setOnClickListener(v -> {
            Intent faqIntent = new Intent(this, FAQActivity.class);
            startActivity(faqIntent);
        });
    }

    // OnTouch method to handle swipe gestures
    private boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialY = event.getY();
                return true;

            case MotionEvent.ACTION_UP:
                float finalY = event.getY();
                float deltaY = finalY - initialY;

                if (Math.abs(deltaY) > SWIPE_THRESHOLD) {
                    if (deltaY > 0) {
                        // Swipe down detected
                        startActivity(new Intent(HelpActivity.this, SettingsActivity.class));
                        finish(); // Close the current activity
                    }
                }
                return true;
        }
        return false;
    }
}
