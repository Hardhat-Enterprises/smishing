package com.example.smishingdetectionapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public abstract class SharedActivity extends AppCompatActivity {

    private static final int SESSION_TIMEOUT_MS = 1200000; // Default 20 minute timer for session timeout // 10 second timer was used to test the toggle switch

    private Handler sessionHandler;
    private Runnable sessionTimeoutRunnable;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "sessionTimeoutPrefs";
    private static final String SWITCH_STATE_KEY = "sessionTimeoutSwitchState";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSessionTimeout();

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        resetSessionTimeout();
    }

    private void setupSessionTimeout() {
        sessionHandler = new Handler();
        sessionTimeoutRunnable = this::showSessionTimeoutPopup;
    }

    private void resetSessionTimeout() {
        boolean isSessionTimeoutEnabled = sharedPreferences.getBoolean(SWITCH_STATE_KEY, false); // sets default state of session timeout as false

        if (isSessionTimeoutEnabled) {
            startSessionTimeout();
        } else {
            stopSessionTimeout();
        }
    }

    private void startSessionTimeout() {
        if (sessionHandler != null) {
            sessionHandler.removeCallbacks(sessionTimeoutRunnable);
            sessionHandler.postDelayed(sessionTimeoutRunnable, SESSION_TIMEOUT_MS);
        }
    }

    private void stopSessionTimeout() {
        if (sessionHandler != null) {
            sessionHandler.removeCallbacks(sessionTimeoutRunnable);
        }
    }

    private void showSessionTimeoutPopup() {
        if (!isFinishing()) {
            PopupSessionTimeout popup = new PopupSessionTimeout();
            popup.setSessionTimeoutListener(this::resetSessionTimeout);
            popup.show(getSupportFragmentManager(), "SessionTimeoutPopup");
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        resetSessionTimeout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sessionHandler != null) {
            sessionHandler.removeCallbacks(sessionTimeoutRunnable);
        }
    }
}
