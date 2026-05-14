package com.example.smishingdetectionapp.riskmeter;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.domain.riskscanner.AndroidRiskCheckProvider;
import com.example.smishingdetectionapp.domain.riskscanner.RiskCheckResult;
import com.example.smishingdetectionapp.domain.riskscanner.RiskScannerEngine;
import com.example.smishingdetectionapp.domain.riskscanner.RiskScore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RiskScannerLogic {

    private static int calculatedScore = 0;

    // this is to list the specific risk factors that were factored for our detected issues feedback
    private static final List<String> triggeredRisks = new ArrayList<>();
    private static @NotNull List<@NotNull RiskCheckResult> checkResults = new ArrayList<>();

    // this is the main method and delegates to shared engine, then updates UI
    public static void scanHabits(
            Context context,
            ProgressBar progressBar,
            TextView riskLevelText,
            View lightAgeGroup, View lightSmsApp, View lightSecurityApp,
            View lightSpamFilter, View lightDeviceLock, View lightUnknownSources,
            View lightSmsBehaviour,
            boolean disableSmsRisk,
            boolean disableAgeRisk,
            boolean disableSecurityRisk,
            int userAge
    ) {
        AndroidRiskCheckProvider provider = new AndroidRiskCheckProvider(context);
        RiskScore score = RiskScannerEngine.INSTANCE.scanHabits(
                provider, disableSmsRisk, disableAgeRisk, disableSecurityRisk, userAge
        );

        calculatedScore = score.getTotalScore();
        triggeredRisks.clear();
        triggeredRisks.addAll(score.getTriggeredRisks());
        checkResults = new ArrayList<>(score.getCheckResults());

        for (RiskCheckResult result : checkResults) {
            String color = result.getPassed() ? "#66BB6A" : "#EF5350";

            switch (result.getName()) {
                case "Age Group":       setLightColor(lightAgeGroup, color); break;
                case "SMS Behaviour":
                case "SMS Behavior":
                    setLightColor(lightSmsBehaviour, color);
                    break;
                case "Security App":    setLightColor(lightSecurityApp, color); break;
                case "Spam Filter":     setLightColor(lightSpamFilter, color); break;
                case "Device Lock":     setLightColor(lightDeviceLock, color); break;
                case "Unknown Sources": setLightColor(lightUnknownSources, color); break;
                case "SMS App":         setLightColor(lightSmsApp, color); break;
            }
        }


        riskLevelText.setText(score.getRiskLevel());
        updateProgressBarColor(progressBar, calculatedScore);

    }

    public static int getCalculatedScore() {
        return calculatedScore;
    }

    public static List<String> getTriggeredRisks() {
        return triggeredRisks;
    }

    // color coordinating the progress bar based on risk level
    private static void updateProgressBarColor(ProgressBar progressBar, int score) {
        if (score <= 30) {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(progressBar.getContext(), R.color.green));
        } else if (score <= 60) {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(progressBar.getContext(), R.color.orange));
        } else {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(progressBar.getContext(), R.color.redd));
        }
    }

    private static void setLightColor(View view, String color) {
        view.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(color)));
    }

}