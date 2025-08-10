package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.Connectivity.ConnectivityMonitor;
import com.example.smishingdetectionapp.databinding.ActivityMainBinding;
import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.detections.DetectionsActivity;
import com.example.smishingdetectionapp.notifications.NotificationPermissionDialogFragment;
import com.example.smishingdetectionapp.riskmeter.RiskScannerTCActivity;
import com.example.smishingdetectionapp.utils.NetworkUtils;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends SharedActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private boolean isBackPressed = false;

    // Offline banner view
    private TextView offlineBanner;

    // For live connectivity updates (to drive the banner instantly)
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Optional heads-up at launch (Mahir)
        if (!NetworkUtils.isConnected(this)) {
            Toast.makeText(this, "You are offline", Toast.LENGTH_LONG).show();
        }

        // 1) Initialize the global connectivity monitor (Mahir)
        ConnectivityMonitor.init(getApplicationContext());

        // 2) Observe connectivity changes (global LiveData) (Mahir)
        ConnectivityMonitor.getIsConnected().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean connected) {
                if (connected != null && connected) {
                    Toast.makeText(MainActivity.this, "✅ Back Online", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "⚠️ Offline Mode Enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // App bar config for NavigationUI
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_report, R.id.nav_news, R.id.nav_settings
        ).build();

        if (!areNotificationsEnabled()) {
            showNotificationPermissionDialog();
        }

        // ===== Offline banner wiring (Pahul) =====
        offlineBanner = findViewById(R.id.offline_banner);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Initialize banner to current state
        checkOfflineMode();
        // ========================================

        // Bottom navigation
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

        // Buttons
        Button debug_btn = findViewById(R.id.debug_btn);
        debug_btn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DebugActivity.class)));

        Button detections_btn = findViewById(R.id.detections_btn);
        detections_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, DetectionsActivity.class));
            finish();
        });

        Button learnMoreButton = findViewById(R.id.fragment_container);
        learnMoreButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, EducationActivity.class)));

        Button scanner_btn = findViewById(R.id.scanner_btn);
        scanner_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, RiskScannerTCActivity.class));
            finish();
        });

        Button radarBtn = findViewById(R.id.radar_btn);
        radarBtn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RadarActivity.class)));

        // Database connection (for totals)
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        TextView infoText = findViewById(R.id.information_text);
        TextView total_count = findViewById(R.id.total_counter);

        infoText.setText("Welcome to Smishing Detection! Your real-time tool to deter and detect smishing attacks.\nYour app is ready to smish.");
        total_count.setText(String.valueOf(databaseAccess.getCounter()));

        databaseAccess.close();

        // Onboarding guide
        boolean showGuideNow = getIntent().getBooleanExtra("showGuide", false);
        if (showGuideNow) {
            findViewById(R.id.debug_btn).post(() -> {
                new TapTargetSequence(MainActivity.this)
                        .targets(
                                TapTarget.forView(findViewById(R.id.new_detections_container), "New Detections", "This shows any newly detected smishing attempts on your device.")
                                        .outerCircleColor(R.color.navy_blue)
                                        .targetCircleColor(android.R.color.white)
                                        .targetRadius(40)
                                        .titleTextSize(22)
                                        .descriptionTextSize(18)
                                        .drawShadow(true)
                                        .cancelable(false)
                                        .transparentTarget(true),

                                TapTarget.forView(findViewById(R.id.total_detections_container), "Total Detections", "This shows the total number of smishing attempts detected on your device.")
                                        .outerCircleColor(R.color.navy_blue)
                                        .targetCircleColor(android.R.color.white)
                                        .targetRadius(40)
                                        .titleTextSize(22)
                                        .descriptionTextSize(18)
                                        .drawShadow(true)
                                        .cancelable(false)
                                        .transparentTarget(true),

                                TapTarget.forView(findViewById(R.id.detections_btn), "View Detections", "Tap here to view detailed records of detected smishing attempts made on your device.")
                                        .outerCircleColor(R.color.navy_blue)
                                        .targetCircleColor(android.R.color.white)
                                        .targetRadius(31)
                                        .titleTextSize(22)
                                        .descriptionTextSize(18)
                                        .drawShadow(true)
                                        .cancelable(false)
                                        .transparentTarget(true),

                                TapTarget.forView(findViewById(R.id.scanner_btn), "Risk Scanner", "Tap here to scan your device and assess how vulnerable it may be to smishing attacks.")
                                        .outerCircleColor(R.color.navy_blue)
                                        .targetCircleColor(android.R.color.white)
                                        .targetRadius(31)
                                        .titleTextSize(22)
                                        .descriptionTextSize(18)
                                        .drawShadow(true)
                                        .cancelable(false)
                                        .transparentTarget(true),

                                TapTarget.forView(findViewById(R.id.fragment_container), "Learn More", "Tap here to explore tips and tutorials to understand smishing and stay safe.")
                                        .outerCircleColor(R.color.navy_blue)
                                        .targetCircleColor(android.R.color.white)
                                        .targetRadius(23)
                                        .titleTextSize(22)
                                        .descriptionTextSize(18)
                                        .drawShadow(true)
                                        .cancelable(false)
                                        .transparentTarget(true),

                                TapTarget.forView(findViewById(R.id.bottom_navigation), "Navigation Bar", "This is the navigation bar. Use it to switch between the Home screen, the Report page to report potential smishing attempts, the News section for the latest smishing updates, and the Settings page.")
                                        .outerCircleColor(R.color.navy_blue)
                                        .targetCircleColor(android.R.color.white)
                                        .targetRadius(30)
                                        .titleTextSize(22)
                                        .descriptionTextSize(18)
                                        .drawShadow(true)
                                        .cancelable(false)
                                        .transparentTarget(true)
                        )
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
            });
        }
    }

    // Register live network callbacks so the banner updates instantly (Pahul)
    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && connectivityManager != null) {
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override public void onAvailable(Network network) {
                    runOnUiThread(() -> checkOfflineMode());
                }
                @Override public void onLost(Network network) {
                    runOnUiThread(() -> checkOfflineMode());
                }
            };
            try {
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
            } catch (Exception ignored) { /* safe-guard */ }
        } else {
            // For older APIs, reflect current state when coming to foreground
            checkOfflineMode();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && connectivityManager != null && networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception ignored) { /* already unregistered or not set */ }
        }
    }

    /** Show/hide the offline banner based on current connectivity (Pahul) */
    private void checkOfflineMode() {
        if (!isOnline()) {
            if (offlineBanner != null) {
                offlineBanner.setVisibility(View.VISIBLE);
                offlineBanner.setText("⚠ You are currently offline. Some features may be limited.");
            }
        } else {
            if (offlineBanner != null) offlineBanner.setVisibility(View.GONE);
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            @SuppressWarnings("deprecation")
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }
        return false;
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
        new Handler().postDelayed(() -> isBackPressed = false, 2000);
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
