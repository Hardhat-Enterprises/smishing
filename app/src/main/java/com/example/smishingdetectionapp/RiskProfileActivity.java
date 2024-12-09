package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RiskProfileActivity extends AppCompatActivity {

    private PieChartView pieChartView;
    private TextView tvRiskScoreValue, tvRiskAdvice, tvFlaggedLinks, tvFlaggedSenders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_profile);

        // Initialize Views
        pieChartView = findViewById(R.id.pie_chart_view);
        tvRiskScoreValue = findViewById(R.id.tv_risk_score_value);
        tvRiskAdvice = findViewById(R.id.tv_risk_advice);
        tvFlaggedLinks = findViewById(R.id.tv_flagged_links);
        tvFlaggedSenders = findViewById(R.id.tv_flagged_senders);

        // Initialize Back Button
        ImageButton backButton = findViewById(R.id.risk_profile_back);
        backButton.setOnClickListener(v -> finish()); // Close this activity and navigate back

        // Simulated data for testing
        int flaggedLinks = 2; // Example value
        int flaggedSenders = 4; // Example value

        // Log debug information
        Log.d("RiskProfileActivity", "Flagged Links: " + flaggedLinks + ", Flagged Senders: " + flaggedSenders);

        // Update risk profile
        updateRiskProfile(flaggedLinks, flaggedSenders);
    }

    @SuppressLint("SetTextI18n")
    private void updateRiskProfile(int flaggedLinks, int flaggedSenders) {
        // Calculate risk score
        int riskScore = (flaggedLinks * 10) + (flaggedSenders * 15); // Example calculation
        if (riskScore > 100) riskScore = 100; // Cap at 100

        // Log debug information
        Log.d("RiskProfileActivity", "Calculated Risk Score: " + riskScore);

        // Update PieChartView with animation
        pieChartView.setProgressWithAnimation(riskScore);

        // Update Risk Score TextView
        tvRiskScoreValue.setText(riskScore + " / 100");

        // Provide advice based on risk level and update color
        if (riskScore >= 60) {
            tvRiskAdvice.setText("High Risk: Immediate action required! Avoid clicking links from unknown senders and report suspicious activity.");
            tvRiskAdvice.setTextColor(getResources().getColor(R.color.red)); // High Risk: Red
        } else if (riskScore > 20) {
            tvRiskAdvice.setText("Medium Risk: Be cautious. Monitor flagged senders and links closely.");
            tvRiskAdvice.setTextColor(getResources().getColor(R.color.yellow)); // Medium Risk: Yellow
        } else {
            tvRiskAdvice.setText("Low Risk: Your account is secure. Continue following good practices.");
            tvRiskAdvice.setTextColor(getResources().getColor(R.color.green)); // Low Risk: Green
        }

        // Update flagged links and senders
        tvFlaggedLinks.setText("Flagged Links: " + flaggedLinks);
        tvFlaggedSenders.setText("Suspicious Senders: " + flaggedSenders);
    }

}
