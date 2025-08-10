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

    /** Hooks for children (optional) */
    protected void onWentOffline() {}
    protected void onBackOnline() {}
    protected void onRetryClicked() {
        // Force a UI reflect using the latest value; real screens can kick API retries here
        Boolean v = ConnectivityMonitor.getIsConnected().getValue();
        reflectConnectivity(Boolean.TRUE.equals(v));
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

    /**
     * Backward-compat shim: some activities may still call super.setupOfflineUI().
     * Keep this method so those calls compile; it simply delegates to the real wiring.
     */
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

        // Initial reflect (value may be null on first launch)
        Boolean v = ConnectivityMonitor.getIsConnected().getValue();
        reflectConnectivity(Boolean.TRUE.equals(v));

        // Observe live changes tied to Activity lifecycle
        ConnectivityMonitor.getIsConnected().observe(this, connected ->
                reflectConnectivity(Boolean.TRUE.equals(connected)));
    }

    protected void reflectConnectivity(boolean isOnline) {
        if (bannerRoot == null) return; // layout doesn’t include a banner; no-op

        if (isOnline) {
            bannerRoot.setVisibility(View.GONE);
            onBackOnline();
            Snackbar.make(bannerRoot, "Back online", Snackbar.LENGTH_SHORT).show();
        } else {
            bannerRoot.setVisibility(View.VISIBLE);
            if (bannerText != null) {
                bannerText.setText("You’re offline. Some features are limited.");
            }
            onWentOffline();
            Snackbar.make(bannerRoot, "Offline mode", Snackbar.LENGTH_SHORT).show();
        }
    }
}
