package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.notifications.NotificationType;

public class NotificationActivity extends SharedActivity {

    private float initialY; // Used for swipe detection
    private static final int SWIPE_THRESHOLD = 50; // Adjusted threshold for swipe detection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the layout defined in 'activity_notification.xml'
        setContentView(R.layout.activity_notification);
        // Enable edge-to-edge display to allow your layout to extend into the window insets
        EdgeToEdge.enable(this);

        // Set up the main view to listen for touch events
        View mainView = findViewById(R.id.notification_main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                // Apply padding for system bars
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                // Consume the system window insets
                return insets.consumeSystemWindowInsets();
            });
        } else {
            Log.e("NotificationActivity", "mainView is null");
        }

        // Create instances of each NotificationType for the switches
        NotificationType smishDetectionAlert = NotificationType.createSmishDetectionAlert(getApplicationContext());
        NotificationType spamDetectionAlert = NotificationType.createSpamDetectionAlert(getApplicationContext());
        NotificationType newsAlerts = NotificationType.createNewsAlert(getApplicationContext());
        NotificationType incidentAlerts = NotificationType.createIncidentAlert(getApplicationContext());
        NotificationType updateNotification = NotificationType.createUpdateNotification(getApplicationContext());
        NotificationType backupNotification = NotificationType.createBackupNotification(getApplicationContext());
        NotificationType passwordNotification = NotificationType.createPasswordNotification(getApplicationContext());

        // Initialize the Switch objects from the layout
        setupSwitch(findViewById(R.id.smishing_notification_switch), smishDetectionAlert);
        setupSwitch(findViewById(R.id.spam_notification_switch), spamDetectionAlert);
        setupSwitch(findViewById(R.id.news_notification_switch), newsAlerts);
        setupSwitch(findViewById(R.id.incident_report_switch), incidentAlerts);
        setupSwitch(findViewById(R.id.update_notification_switch), updateNotification);
        setupSwitch(findViewById(R.id.backup_reminder_switch), backupNotification);
        setupSwitch(findViewById(R.id.password_security_check_switch), passwordNotification);

        // Setup for the back button
        ImageButton notification_back = findViewById(R.id.notification_back);
        if (notification_back != null) {
            notification_back.setOnClickListener(v -> {
                startActivity(new Intent(this, SettingsActivity.class));
                finish(); // Close the current activity
            });
        } else {
            Log.e("NotificationActivity", "Back button is null");
        }

        // Setup for button that takes you to notification settings in your device
        Button settingsButton = findViewById(R.id.open_notification_settings_button);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            } else {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", getPackageName(), null));
            }
            startActivity(intent);
        });
    }

    // Method to setup switch with a NotificationType object
    private void setupSwitch(Switch switchButton, NotificationType notificationType) {
        if (switchButton != null) {
            switchButton.setChecked(notificationType.getEnabled());
            switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                notificationType.setEnabled(isChecked);
            });
        } else {
            Log.e("NotificationActivity", "Switch button is null");
        }
    }

    // OnTouch method to handle swipe gestures
    private boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialY = event.getY();
                return true;

            case MotionEvent.ACTION_UP:
                float finalY = event.getY();
                float deltaY = finalY - initialY;

                if (Math.abs(deltaY) > SWIPE_THRESHOLD) {
                    if (deltaY > 0) {
                        // Swipe down detected
                        Intent intent = new Intent(NotificationActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        finish(); // Close the current activity
                    }
                }
                return true;
        }
        return false;
    }
}
