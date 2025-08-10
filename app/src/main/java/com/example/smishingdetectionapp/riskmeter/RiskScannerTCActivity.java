package com.example.smishingdetectionapp.riskmeter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.NewsActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.example.smishingdetectionapp.Connectivity.ConnectivityMonitor;
import com.example.smishingdetectionapp.ui.BaseOfflineActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class RiskScannerTCActivity extends BaseOfflineActivity {

    private Switch smsSwitch, ageSwitch, securitySwitch;
    private Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_scanner_tc);

        // ----- Bottom navigation -----
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
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

        // Back button
        ImageButton back = findViewById(R.id.riskscannertc_back);
        back.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Toggles
        smsSwitch = findViewById(R.id.switch_sms);
        ageSwitch = findViewById(R.id.switch_age);
        securitySwitch = findViewById(R.id.switch_security);

        // Scan button
        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(v -> {
            boolean online = Boolean.TRUE.equals(ConnectivityMonitor.getIsConnected().getValue());
            if (!online) {
                Snackbar.make(v, "Offline — network scan unavailable", Snackbar.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, RiskScannerActivity.class);
            intent.putExtra("DISABLE_SMS_RISK",       smsSwitch.isChecked());       // true = disable SMS risk
            intent.putExtra("DISABLE_AGE_RISK",       ageSwitch.isChecked());       // true = disable age risk
            intent.putExtra("DISABLE_SECURITY_RISK",  securitySwitch.isChecked());  // true = disable security checks
            startActivity(intent);
        });

        // Reflect initial state for connectivity
        reflectUiForConnectivity();
    }

    /** Called by BaseOfflineActivity when we go offline */
    @Override
    protected void onWentOffline() {
        reflectUiForConnectivity();
    }

    /** Called by BaseOfflineActivity when we come back online */
    @Override
    protected void onBackOnline() {
        reflectUiForConnectivity();
    }

    private void reflectUiForConnectivity() {
        boolean online = Boolean.TRUE.equals(ConnectivityMonitor.getIsConnected().getValue());
        if (scanButton != null) {
            // You can either disable or just dim. Here we do both to make it unambiguous.
            scanButton.setEnabled(online);
            scanButton.setAlpha(online ? 1f : 0.6f);
        }
    }
}
