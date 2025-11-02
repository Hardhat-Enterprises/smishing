package com.example.smishingdetectionapp.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.Connectivity.ConnectivityMonitor;
import com.example.smishingdetectionapp.R;
import com.google.android.material.snackbar.Snackbar;

public abstract class BaseOfflineActivity extends AppCompatActivity {

    private View bannerRoot;
    private TextView bannerText;
    private Button retryBtn;

    // Track last known state shown on this Activity
    private Boolean lastOnlineShown = null;

    // We ignore the very first emission from LiveData to avoid a “Back online” toast at login
    private boolean hasObservedOnce = false;

    /** Optional hooks for children */
    protected void onWentOffline() {}
    protected void onBackOnline() {}
    protected void onRetryClicked() {
        Boolean v = ConnectivityMonitor.getIsConnected().getValue();
        reflectBanner(Boolean.TRUE.equals(v)); // no snackbar on manual retry
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // do NOT touch views here
    }

    /** Ensure our wiring runs immediately after any setContentView(...) usage */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setupOfflineUIAndObserver();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setupOfflineUIAndObserver();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        setupOfflineUIAndObserver();
    }

    /** Back-compat shim for older calls */
    protected final void setupOfflineUI() {
        setupOfflineUIAndObserver();
    }

    private void setupOfflineUIAndObserver() {
        bannerRoot = findViewById(R.id.offline_banner);
        bannerText = findViewById(R.id.offline_banner_text);
        retryBtn   = findViewById(R.id.offline_retry);

        if (retryBtn != null) {
            retryBtn.setOnClickListener(v -> onRetryClicked());
        }

        // Initial reflect: set banner correctly but DO NOT show any snackbar
        Boolean initial = ConnectivityMonitor.getIsConnected().getValue();
        boolean online = Boolean.TRUE.equals(initial);
        reflectBanner(online);
        lastOnlineShown = online;

        // Observe live changes tied to Activity lifecycle
        ConnectivityMonitor.getIsConnected().observe(this, connected -> {
            boolean isOnline = Boolean.TRUE.equals(connected);

            // Ignore the very first LiveData emission (often repeats the initial state)
            if (!hasObservedOnce) {
                hasObservedOnce = true;
                lastOnlineShown = isOnline;
                reflectBanner(isOnline); // keep banner correct, no snackbar
                return;
            }

            // Update banner first (always)
            reflectBanner(isOnline);

            // Only show snackbar on a real transition from OFFLINE -> ONLINE
            if (lastOnlineShown != null && !lastOnlineShown && isOnline) {
                onBackOnline();
                // Show “Back online” once for this transition
                if (bannerRoot != null) {
                    Snackbar.make(bannerRoot, "Back online", Snackbar.LENGTH_SHORT).show();
                }
            }

            // Remember last state
            lastOnlineShown = isOnline;

            // Note: we intentionally do NOT show a snackbar going ONLINE -> OFFLINE
            // The banner itself is a strong enough signal and avoids noise.
        });
    }

    /** Just keeps the banner visibility/text correct (never shows snackbars) */
    private void reflectBanner(boolean isOnline) {
        if (bannerRoot == null) return;

        if (isOnline) {
            bannerRoot.setVisibility(View.GONE);
        } else {
            bannerRoot.setVisibility(View.VISIBLE);
            if (bannerText != null) {
                bannerText.setText("You’re offline. Some features are limited.");
            }
            onWentOffline(); // give child Activities a hook if they want to dim buttons, etc.
        }
    }
}
