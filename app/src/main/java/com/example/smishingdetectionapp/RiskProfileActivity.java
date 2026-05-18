package com.example.smishingdetectionapp;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class RiskProfileActivity extends AppCompatActivity {

    private ProgressBar riskProgressBar, barLoginAttempts, barPinResets, barProfileCompleteness;
    private TextView tvRiskScoreValue, tvRiskDetails, tvRecommendations, tvRecentActivity, tvSecurityStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_profile);

        // Back
        ImageButton backBtn = findViewById(R.id.risk_back);
        backBtn.setOnClickListener(v -> onBackPressed());

        // Bind
        riskProgressBar = findViewById(R.id.riskProgressBar);
        tvRiskScoreValue = findViewById(R.id.tv_risk_score_value);
        tvRiskDetails = findViewById(R.id.tv_risk_details);

        barLoginAttempts = findViewById(R.id.bar_login_attempts);
        barPinResets = findViewById(R.id.bar_pin_resets);
        barProfileCompleteness = findViewById(R.id.bar_profile_completeness);

        tvRecommendations = findViewById(R.id.tv_recommendations);
        tvRecentActivity = findViewById(R.id.tv_recent_activity);
        tvSecurityStatus = findViewById(R.id.tv_security_status);
        Button btnImprove = findViewById(R.id.btn_improve_security);

        // Sample data (replace with real)
        int riskScore = 65;
        int loginAttemptsScore = 48;
        int pinResetsScore = 22;
        int profileCompletenessScore = 82;

        // Animate bars
        animateBar(riskProgressBar, riskScore, 1800);
        animateBar(barLoginAttempts, loginAttemptsScore, 1400);
        animateBar(barPinResets, pinResetsScore, 1400);
        animateBar(barProfileCompleteness, profileCompletenessScore, 1400);

        // Texts
        tvRiskScoreValue.setText(riskScore + "/100");

        String riskLevel = getRiskLevel(riskScore);
        tvRiskDetails.setText("Risk Level: " + riskLevel);
        tvRiskDetails.setTextColor(ContextCompat.getColor(
                this,
                riskLevel.equals("High")   ? android.R.color.holo_red_dark :
                        riskLevel.equals("Medium") ? android.R.color.holo_orange_dark :
                                android.R.color.holo_green_dark
        ));

        // Bulleted sections
        if (riskLevel.equals("High")) {
            tvRecommendations.setText("• Change your PIN immediately\n• Enable Two-Factor Authentication\n• Review login history");
        } else if (riskLevel.equals("Medium")) {
            tvRecommendations.setText("• Enable Two-Factor Authentication\n• Avoid public Wi-Fi logins");
        } else {
            tvRecommendations.setText("• Keep monitoring activity\n• Great job maintaining security!");
        }

        tvRecentActivity.setText("• 3 logins this week\n• 1 PIN reset\n• 0 failed attempts");
        tvSecurityStatus.setText("✅ PIN is set\n⚠️ No 2FA enabled\n✅ Profile details are complete");

        btnImprove.setOnClickListener(v ->
                Toast.makeText(this, "Redirecting to Security Settings...", Toast.LENGTH_SHORT).show()
        );
    }

    private void animateBar(ProgressBar bar, int value, int durationMs) {
        ObjectAnimator anim = ObjectAnimator.ofInt(bar, "progress", 0, value);
        anim.setDuration(durationMs);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
    }

    private String getRiskLevel(int score) {
        if (score < 30) return "Low";
        else if (score < 70) return "Medium";
        else return "High";
    }
}
