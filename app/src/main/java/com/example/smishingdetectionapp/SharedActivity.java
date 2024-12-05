package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.smishingdetectionapp.ui.login.LoginActivity;
import androidx.appcompat.app.AppCompatActivity;

public abstract class SharedActivity extends AppCompatActivity {
    private static final int SESSION_TIMEOUT_MS = 10000; // 10 seconds timeout
    private Handler sessionHandler;
    private Runnable sessionTimeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSessionTimeout();
    }

    private void setupSessionTimeout() {
        sessionHandler = new Handler();
        sessionTimeoutRunnable = this::onSessionTimeout;
        resetSessionTimeout();
    }

    private void resetSessionTimeout() {
        if (sessionHandler != null) {
            sessionHandler.removeCallbacks(sessionTimeoutRunnable);
            sessionHandler.postDelayed(sessionTimeoutRunnable, SESSION_TIMEOUT_MS);
        }
    }

    private void onSessionTimeout() {
        if (!isFinishing()) {
            Intent intent = new Intent(SharedActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
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