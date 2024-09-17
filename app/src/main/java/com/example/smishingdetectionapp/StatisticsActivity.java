package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.sms.SMSExtractor;
import com.example.smishingdetectionapp.sms.OnStatsUpdateListener;

public class StatisticsActivity extends AppCompatActivity implements OnStatsUpdateListener {

    private TextView totalMessagesTextView;
    private TextView smishingMessagesTextView;
    private Button refreshButton;
    private ProgressBar messagesProgressBar;
    private SMSExtractor smsExtractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_statistics);

        smsExtractor = new SMSExtractor(getApplicationContext());
        smsExtractor.setOnStatsUpdateListener(this); // Set the listener

        totalMessagesTextView = findViewById(R.id.totalMessagesTextView);
        smishingMessagesTextView = findViewById(R.id.smishingMessagesTextView);
        refreshButton = findViewById(R.id.refreshButton);

        // Trigger statistics update
        updateStatistics();
        messagesProgressBar = findViewById(R.id.messagesProgressBar);
        refreshButton.setOnClickListener(v -> updateStatistics());


        ImageButton back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(v -> finish());

    }

    // Update the statistics by calling the extraction methods
    private void updateStatistics() {
        // Assuming that SMSExtractor methods are asynchronous and will update the listener
        smsExtractor.extractAllMessages();  // Trigger the update
        smsExtractor.extractSuspiciousMessages();  // Trigger the update
    }


    // This method will be called when the stats are updated
    @Override
    public void onStatsUpdated(int totalMessagesAnalyzed, int smishingMessagesCount) {
        // Display total and smishing message counts
        totalMessagesTextView.setText("Total Messages Analyzed: " + totalMessagesAnalyzed);
        smishingMessagesTextView.setText("Smishing Messages Count: " + smishingMessagesCount);

        if (totalMessagesAnalyzed > 0 && smishingMessagesCount > 0) {
            try {
                // Calculate the percentage and update the progress bar
                int progress = (int) (((double) smishingMessagesCount / totalMessagesAnalyzed) * 100);
                Log.d("StatisticsActivity", "Progress: " + progress);  // Debugging: log progress value
                messagesProgressBar.setProgress(progress);

                // Set the progress bar tint dynamically (Green if safe, red if smishing exists)
                if (smishingMessagesCount > 0) {
                    messagesProgressBar.setProgressTintList(getResources().getColorStateList(R.color.red, null));
                } else {
                    messagesProgressBar.setProgressTintList(getResources().getColorStateList(R.color.green, null));
                }
            } catch (Exception e) {
                Log.e("StatisticsActivity", "Error while setting progress: ", e);
            }
        } else {
            // If no messages, show 100% progress (fully green) and 0 for both counts
            totalMessagesTextView.setText("Total Messages Analyzed: 0");
            smishingMessagesTextView.setText("Smishing Messages Count: 0");
        }
    }
}

