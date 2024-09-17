package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.sms.SMSExtractor;

public class StatisticsActivity extends AppCompatActivity {

    private TextView totalMessagesTextView;
    private TextView smishingMessagesTextView;
    private Button refreshButton;

    private SMSExtractor smsExtractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_statistics);

        // Initialize SMSExtractor (assuming you have a way to get context)
        smsExtractor = new SMSExtractor(getApplicationContext());

        totalMessagesTextView = findViewById(R.id.totalMessagesTextView);
        smishingMessagesTextView = findViewById(R.id.smishingMessagesTextView);
        refreshButton = findViewById(R.id.refreshButton);

        updateStatistics();

        refreshButton.setOnClickListener(v -> updateStatistics());

        ImageButton back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(v -> {
            finish();
        });
    }

    private void updateStatistics() {
        // Call methods to get the latest statistics
        int totalMessagesAnalyzed = smsExtractor.getTotalMessagesAnalyzed();
        int smishingMessagesCount = smsExtractor.getSmishingMessagesCount();

        // Update TextViews with the latest values
        totalMessagesTextView.setText("Total Messages Analyzed: " + totalMessagesAnalyzed);
        smishingMessagesTextView.setText("Smishing Messages Count: " + smishingMessagesCount);
    }
}
