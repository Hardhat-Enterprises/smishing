package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.navigation.BottomNavCoordinator;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TopicDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);

        ImageButton backButton = findViewById(R.id.backButton);
        TextView titleView = findViewById(R.id.topicTitle);
        TextView contentView = findViewById(R.id.topicContent);

        Intent intent = getIntent();
        String topicId = intent.getStringExtra("TOPIC_ID");

        // back button
        backButton.setOnClickListener(v -> finish());

        // set content depends on Id
        switch (topicId) {
            case "DETECT_SMISHING":
                setupDetectSmishing(titleView, contentView);
                break;
            case "REPORT_SMS":
                setupReportSms(titleView, contentView);
                break;
            case "SMISHING_VS_PHISHING":
                setupSmishingVsPhishing(titleView, contentView);
                break;
        }

        BottomNavCoordinator.setup(this, R.id.nav_settings);
    }

    // How to detect a smishing message method
    private void setupDetectSmishing(TextView title, TextView content) {
        title.setText(R.string.detect_smishing_title);
        content.setText(R.string.detect_smishing_content);
    }

    // How to report a suspicious SMS
    private void setupReportSms(TextView title, TextView content) {
        title.setText(R.string.report_sms_title);
        content.setText(R.string.report_sms_content);
    }

    // What is smishing vs. phishing
    private void setupSmishingVsPhishing(TextView title, TextView content) {
        title.setText(R.string.smishing_vs_phishing_title);
        content.setText(R.string.smishing_vs_phishing_content);
    }

    // New topics...
}