package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.smishingdetectionapp.news.NewsActivity;
import com.example.smishingdetectionapp.ui.account.AccountActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.biometric.BiometricPrompt;
import androidx.biometric.BiometricManager;

import java.util.concurrent.Executor;

public class SettingsActivity extends AppCompatActivity {

    private BiometricPrompt biometricPrompt;
    private boolean isAuthenticated = false;
    private static final int TIMEOUT_MILLIS = 30000; // 30 seconds timeout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Bottom Navigation setup
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_settings);

        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                return true;
            }
            return false;
        });

        // Account button with biometric authentication
        Button accountBtn = findViewById(R.id.accountBtn);
        accountBtn.setOnClickListener(v -> triggerBiometricAuthenticationWithTimeout());

        // Filtering button to switch to Smishing rules page
        Button filteringBtn = findViewById(R.id.filteringBtn);
        filteringBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, SmishingRulesActivity.class));
            finish();
        });

        // Report button to switch to Reporting page
        Button reportBtn = findViewById(R.id.reportBtn);
        reportBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ReportingActivity.class));
            finish();
        });

        // Notification button to switch to Notification page
        Button notificationsButton = findViewById(R.id.notificationsBtn);
        notificationsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
            finish();
        });

        // Help button to switch to Help page
        Button helpBtn = findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, HelpActivity.class));
            finish();
        });

        // About Me button to switch to AboutMeActivity
        Button aboutMeButton = findViewById(R.id.aboutMeBtn);
        aboutMeButton.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutMeActivity.class));
        });

        // About Us button to switch to AboutUsActivity
        Button aboutUsBtn = findViewById(R.id.aboutUsBtn);
        aboutUsBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutUsActivity.class));
        });

        // Chat Assistant button to switch to ChatAssistantActivity
        Button chatAssistantBtn = findViewById(R.id.chatAssistantBtn);
        chatAssistantBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ChatAssistantActivity.class));
        });

        // Feedback button to switch to FeedbackActivity
        Button feedbackBtn = findViewById(R.id.feedbackBtn);
        feedbackBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, FeedbackActivity.class));
            finish();
        });

        // Forum button to switch to ForumActivity
        Button forumBtn = findViewById(R.id.forumBtn);
        forumBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ForumActivity.class));
            finish();
        });
    }

    // Trigger biometric authentication with timeout
    private void triggerBiometricAuthenticationWithTimeout() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentication Required")
                .setDescription("Please authenticate to access your account settings")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();

        // Start the authentication process
        biometricPrompt = getPrompt();
        biometricPrompt.authenticate(promptInfo);

        // Start the timeout timer
        startTimeoutTimer();
    }

    // BiometricPrompt setup
    private BiometricPrompt getPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser("Authentication Error: " + errString);
                redirectToSettingsActivity();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                notifyUser("Authentication Succeeded!");
                isAuthenticated = true; // Mark as authenticated
                openAccountActivity(); // Proceed to AccountActivity
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                notifyUser("Authentication Failed");
            }
        };

        return new BiometricPrompt(this, executor, callback);
    }

    // Start a timeout timer for authentication
    private void startTimeoutTimer() {
        new Handler().postDelayed(() -> {
            if (!isAuthenticated) { // If authentication hasn't occurred within the timeout
                notifyUser("Authentication timed out. Redirecting to Settings...");
                biometricPrompt.cancelAuthentication(); // Cancel the ongoing authentication
                redirectToSettingsActivity(); // Redirect to SettingsActivity on timeout
            }
        }, TIMEOUT_MILLIS);
    }

    // Redirect to SettingsActivity
    private void redirectToSettingsActivity() {
        Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish(); // Ensure the current activity is closed
    }

    // Open AccountActivity
    private void openAccountActivity() {
        Intent intent = new Intent(SettingsActivity.this, AccountActivity.class);
        startActivity(intent);
        finish(); // Close SettingsActivity if AccountActivity is opened
    }

    // Show a toast message
    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

