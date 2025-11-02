package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.smishingdetectionapp.ui.login.LoginActivity;
import android.content.Context;
import android.content.res.Configuration;


import android.content.Context;
import android.content.res.Configuration;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;


public abstract class SharedActivity extends AppCompatActivity {
    private static final int SESSION_TIMEOUT_MS = 1200000; // 20 minutes
    private static final int POPUP_TIMEOUT_MS = 30000; // 30 seconds

    private Handler sessionHandler;
    private Handler popupHandler;
    private Runnable sessionTimeoutRunnable;
    private Runnable popupTimeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isBold = prefs.getBoolean("bold_text_enabled", false);
        setTheme(isBold ? R.style.Theme_SmishingDetectionApp_Bold : R.style.Theme_SmishingDetectionApp);
        super.onCreate(savedInstanceState);
        setupSessionTimeout();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        float scale = PreferencesUtil.getTextScale(newBase);
        Configuration config = newBase.getResources().getConfiguration();
        config.fontScale = scale;

        Context scaledContext = newBase.createConfigurationContext(config);
        super.attachBaseContext(scaledContext);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("bold_text_enabled", false)) {
            View root = findViewById(android.R.id.content);
            applyBoldToAllWidgets(root);
        }
    }

    private void applyBoldToAllWidgets(View root) {
        if (!(root instanceof ViewGroup)) return;

        ViewGroup group = (ViewGroup) root;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);

            if (child instanceof Switch || child instanceof androidx.appcompat.widget.SwitchCompat) {
                ((TextView) child).setTypeface(null, Typeface.BOLD);
            }

            if (child instanceof Button || child instanceof com.google.android.material.button.MaterialButton) {
                ((TextView) child).setTypeface(null, Typeface.BOLD);
            }

            if (child instanceof ViewGroup) {
                applyBoldToAllWidgets(child);
            }
        }
    }


    private void setupSessionTimeout() {
        sessionHandler = new Handler();
        sessionTimeoutRunnable = this::onSessionTimeout;
        popupHandler = new Handler();
        popupTimeoutRunnable = this::onPopupTimeout;
        resetSessionTimeout();
    }

    private void resetSessionTimeout() {
        if (sessionHandler != null) {
            sessionHandler.removeCallbacks(sessionTimeoutRunnable);
            sessionHandler.postDelayed(sessionTimeoutRunnable, SESSION_TIMEOUT_MS);
        }
    }

    private void onSessionTimeout() {
        if (!isFinishing() && shouldShowSessionTimeoutPopup()) {
            showSessionTimeoutPopup();
        }
    }

    protected boolean shouldShowSessionTimeoutPopup() {
        return true;
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
        if (popupHandler != null) {
            popupHandler.removeCallbacks(popupTimeoutRunnable);
        }
    }

    private void showSessionTimeoutPopup() {
        PopupSessionTimeout popup = new PopupSessionTimeout();
        popup.setSessionTimeoutListener(this::resetSessionTimeout);

        if (!isFinishing() && !isDestroyed()) {
            try {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(popup, "SessionTimeoutPopup");
                ft.commitAllowingStateLoss(); // FIX: avoids crash after onSaveInstanceState
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        popupHandler.removeCallbacks(popupTimeoutRunnable);
        popupHandler.postDelayed(popupTimeoutRunnable, POPUP_TIMEOUT_MS);
    }

    private void onPopupTimeout() {
        if (!isFinishing()) {
            logout();
        }
    }

    private void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
