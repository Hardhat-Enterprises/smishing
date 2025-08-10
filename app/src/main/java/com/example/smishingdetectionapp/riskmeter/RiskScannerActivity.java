package com.example.smishingdetectionapp.riskmeter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.compose.ui.platform.ComposeView;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.NewsActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.example.smishingdetectionapp.Connectivity.ConnectivityMonitor;
import com.example.smishingdetectionapp.ui.BaseOfflineActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import static com.example.smishingdetectionapp.riskmeter.PulseInjectorKt.injectPulsing;

public class RiskScannerActivity extends BaseOfflineActivity {

    private static final long SCAN_DURATION_MS = 9000L;

    private ComposeView pulseView;
    private TextView scanningText;

    private boolean disableSmsRisk;
    private boolean disableAgeRisk;
    private boolean disableSecurityRisk;

    private final Handler handler = new Handler();
    private Runnable proceedToResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riskscanner);

        // UI
        pulseView     = findViewById(R.id.pulseComposeView);
        scanningText  = findViewById(R.id.scanningText);
        injectPulsing(pulseView);  // Compose pulse animation

        // Read options from TC screen
        disableSmsRisk      = getIntent().getBooleanExtra("DISABLE_SMS_RISK", false);
        disableAgeRisk      = getIntent().getBooleanExtra("DISABLE_AGE_RISK", false);
        disableSecurityRisk = getIntent().getBooleanExtra("DISABLE_SECURITY_RISK", false);

        // Bottom navigation
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (id == R.id.nav_report) {
                Intent i = new Intent(this, CommunityReportActivity.class);
                i.putExtra("source", "home");
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (id == R.id.nav_news) {
                // ✅ Open News (was Settings by mistake)
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        // Back
        ImageButton back = findViewById(R.id.RiskScanner_back);
        back.setOnClickListener(v -> {
            startActivity(new Intent(this, RiskScannerTCActivity.class));
            finish();
        });

        // Prepare runnable once
        proceedToResults = () -> {
            Intent intent = new Intent(RiskScannerActivity.this, RiskResultActivity.class);
            intent.putExtra("DISABLE_SMS_RISK",       disableSmsRisk);
            intent.putExtra("DISABLE_AGE_RISK",       disableAgeRisk);
            intent.putExtra("DISABLE_SECURITY_RISK",  disableSecurityRisk);
            startActivity(intent);
            finish();
        };

        // Kick off (or wait) based on current connectivity
        startScanIfOnline();
    }

    // ---- BaseOfflineActivity hooks ----
    @Override
    protected void onWentOffline() {
        pauseScanUi();
        Snackbar.make(pulseView != null ? pulseView : findViewById(android.R.id.content),
                "Offline — connect to run risk scan", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onBackOnline() {
        startScanIfOnline();
    }

    private void startScanIfOnline() {
        boolean online = Boolean.TRUE.equals(ConnectivityMonitor.getIsConnected().getValue());
        if (!online) {
            pauseScanUi();
            return;
        }

        // Show scanning UI and schedule result transition
        if (scanningText != null) scanningText.setVisibility(View.VISIBLE);
        if (pulseView != null)    pulseView.setVisibility(View.VISIBLE);

        // Ensure we don't double-post after a reconnect
        handler.removeCallbacks(proceedToResults);
        handler.postDelayed(proceedToResults, SCAN_DURATION_MS);
    }

    private void pauseScanUi() {
        // Hide animation / text and cancel any pending transition
        if (scanningText != null) scanningText.setVisibility(View.GONE);
        if (pulseView != null)    pulseView.setVisibility(View.GONE);
        handler.removeCallbacks(proceedToResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
