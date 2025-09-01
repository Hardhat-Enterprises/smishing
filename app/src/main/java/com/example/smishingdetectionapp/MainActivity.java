package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.databinding.ActivityMainBinding;
import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.detections.DetectionsActivity;
import com.example.smishingdetectionapp.RadarActivity;
import com.example.smishingdetectionapp.notifications.NotificationPermissionDialogFragment;
import com.example.smishingdetectionapp.riskmeter.RiskScannerTCActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SharedActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private boolean isBackPressed = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_report, R.id.nav_news, R.id.nav_settings
        ).build();

        if (!areNotificationsEnabled()) {
            showNotificationPermissionDialog();
        }

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_report) {
                startActivity(new Intent(getApplicationContext(), CommunityReportActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        Button debugBtn = findViewById(R.id.debug_btn);
        if (debugBtn != null) {
            debugBtn.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, DebugActivity.class))
            );
        }

        // ===== CLICK TARGETS FOR "VIEW DETECTIONS" AND "RISK SCANNER" =====
        // Prefer the new card containers if present; otherwise fall back to legacy buttons.
        View detectionsClickTarget =
                findViewById(R.id.view_detections_container) != null
                        ? findViewById(R.id.view_detections_container)
                        : findViewById(R.id.detections_btn); // legacy overlay button

        if (detectionsClickTarget != null) {
            detectionsClickTarget.setOnClickListener(v -> {
                startActivity(new Intent(this, DetectionsActivity.class));
                finish();
            });
        }

        View scannerClickTarget =
                findViewById(R.id.risk_scanner_container) != null
                        ? findViewById(R.id.risk_scanner_container)
                        : findViewById(R.id.scanner_btn); // legacy overlay button

        if (scannerClickTarget != null) {
            scannerClickTarget.setOnClickListener(v -> {
                startActivity(new Intent(this, RiskScannerTCActivity.class));
                finish();
            });
        }
        // ===== END CLICK TARGETS =====

        // Learn more
        View learnMoreButton = findViewById(R.id.fragment_container);
        if (learnMoreButton != null) {
            learnMoreButton.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, EducationActivity.class))
            );
        }

        Button radarBtn = findViewById(R.id.radar_btn);
        if (radarBtn != null) {
            radarBtn.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, RadarActivity.class))
            );
        }

        // Database connection
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        TextView infoText = findViewById(R.id.information_text);
        TextView totalCount = findViewById(R.id.total_counter);

        if (infoText != null) {
            infoText.setText("Welcome to Smishing Detection! Your real-time tool to deter and detect smishing attacks.\nYour app is ready to smish.");
        }
        if (totalCount != null) {
            totalCount.setText(String.valueOf(databaseAccess.getCounter()));
        }

        databaseAccess.close();

        // TapTarget guide
        boolean showGuideNow = getIntent().getBooleanExtra("showGuide", false);
        if (showGuideNow && debugBtn != null) {
            debugBtn.post(() -> {
                View ttNew   = findViewById(R.id.new_detections_container);
                View ttTotal = findViewById(R.id.total_detections_container);
                View ttView  = (findViewById(R.id.view_detections_container) != null)
                        ? findViewById(R.id.view_detections_container)
                        : findViewById(R.id.detections_btn);
                View ttScan  = (findViewById(R.id.risk_scanner_container) != null)
                        ? findViewById(R.id.risk_scanner_container)
                        : findViewById(R.id.scanner_btn);
                View ttLearn = findViewById(R.id.fragment_container);
                View ttNav   = findViewById(R.id.bottom_navigation);

                List<TapTarget> targets = new ArrayList<>();
                if (ttNew != null)   targets.add(TapTarget.forView(ttNew, "New Detections", "This shows any newly detected smishing attempts on your device.")
                        .outerCircleColor(R.color.navy_blue).targetCircleColor(android.R.color.white)
                        .targetRadius(40).titleTextSize(22).descriptionTextSize(18)
                        .drawShadow(true).cancelable(false).transparentTarget(true));

                if (ttTotal != null) targets.add(TapTarget.forView(ttTotal, "Total Detections", "This shows the total number of smishing attempts detected on your device.")
                        .outerCircleColor(R.color.navy_blue).targetCircleColor(android.R.color.white)
                        .targetRadius(40).titleTextSize(22).descriptionTextSize(18)
                        .drawShadow(true).cancelable(false).transparentTarget(true));

                if (ttView != null)  targets.add(TapTarget.forView(ttView, "View Detections", "Tap here to view detailed records of detected smishing attempts made on your device.")
                        .outerCircleColor(R.color.navy_blue).targetCircleColor(android.R.color.white)
                        .targetRadius(31).titleTextSize(22).descriptionTextSize(18)
                        .drawShadow(true).cancelable(false).transparentTarget(true));

                if (ttScan != null)  targets.add(TapTarget.forView(ttScan, "Risk Scanner", "Tap here to scan your device and assess how vulnerable it may be to smishing attacks.")
                        .outerCircleColor(R.color.navy_blue).targetCircleColor(android.R.color.white)
                        .targetRadius(31).titleTextSize(22).descriptionTextSize(18)
                        .drawShadow(true).cancelable(false).transparentTarget(true));

                if (ttLearn != null) targets.add(TapTarget.forView(ttLearn, "Learn More", "Tap here to explore tips and tutorials to understand smishing and stay safe.")
                        .outerCircleColor(R.color.navy_blue).targetCircleColor(android.R.color.white)
                        .targetRadius(23).titleTextSize(22).descriptionTextSize(18)
                        .drawShadow(true).cancelable(false).transparentTarget(true));

                if (ttNav != null)   targets.add(TapTarget.forView(ttNav, "Navigation Bar", "This is the navigation bar. Use it to switch between the Home screen, the Report page to report potential smishing attempts, the News section for the latest smishing updates, and the Settings page.")
                        .outerCircleColor(R.color.navy_blue).targetCircleColor(android.R.color.white)
                        .targetRadius(30).titleTextSize(22).descriptionTextSize(18)
                        .drawShadow(true).cancelable(false).transparentTarget(true));

                if (!targets.isEmpty()) {
                    new TapTargetSequence(MainActivity.this)
                            .targets(targets)
                            .listener(new TapTargetSequence.Listener() {
                                @Override
                                public void onSequenceFinish() {
                                    Toast.makeText(MainActivity.this, "You're all set to smish!", Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) { }
                                @Override
                                public void onSequenceCanceled(TapTarget lastTarget) {
                                    Toast.makeText(MainActivity.this, "Guide cancelled", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .start();
                }
            });
        }
    }

    // Press back twice to exit
    @Override
    public void onBackPressed() {
        if (isBackPressed) {
            super.onBackPressed();
            return;
        }
        Toast.makeText(this, "press back again to exit", Toast.LENGTH_SHORT).show();
        isBackPressed = true;
        new Handler(Looper.getMainLooper()).postDelayed(() -> isBackPressed = false, 2000);
    }

    private boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    private void showNotificationPermissionDialog() {
        NotificationPermissionDialogFragment dialogFragment = new NotificationPermissionDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "notificationPermission");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}

