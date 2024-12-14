package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SmishingRulesActivity extends SharedActivity {

    private float initialY; // Variable to track the initial Y position for swipe detection
    private static final int SWIPE_THRESHOLD = 50; // Threshold for swipe detection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_smishing_rules);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button to go back to settings dashboard
        ImageButton report_back = findViewById(R.id.filtering_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // Set OnTouchListener to detect swipe gestures on the ScrollView
        findViewById(R.id.scroll_view).setOnTouchListener(this::onTouch);
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
                        startActivity(new Intent(SmishingRulesActivity.this, SettingsActivity.class));
                        finish(); // Close the current activity
                    }
                }
                return true;
        }
        return false;
    }
}