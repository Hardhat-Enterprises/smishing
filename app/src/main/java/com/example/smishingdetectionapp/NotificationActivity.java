package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.core.view.GestureDetectorCompat;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.notifications.NotificationType;

public class NotificationActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the layout defined in 'activity_notification.xml'
        setContentView(R.layout.activity_notification);
        // Enable edge-to-edge display to allow your layout to extend into the window insets
        EdgeToEdge.enable(this);

        // Initialize GestureDetector
        gestureDetector = new GestureDetectorCompat(this, new GestureListener());

        // Set up the main view to listen for touch events
        View mainView = findViewById(R.id.notification_main);
        mainView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        // Create instances of each NotificationType for the switch
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

        // Additional UI setup
        // Check if the main view is not null to safely apply window insets
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                // Retrieve insets for system bars and apply them as padding to the main view
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                // Consume the system window insets so they are not applied elsewhere
                return insets.consumeSystemWindowInsets();
            });
        } else {
            // Log an error if the main view is not found
            Log.e("NotificationActivity", "mainView is null");
        }

        // Setup for the back button
        ImageButton notification_back = findViewById(R.id.notification_back);
        // Check if the back button is initialized properly
        if (notification_back != null) {
            // Set an onClick listener to handle the back button's behavior
            notification_back.setOnClickListener(v -> {
                // Start SettingsActivity when back button is pressed
                startActivity(new Intent(this, SettingsActivity.class));
                // Close the current activity
                finish();
            });
        } else {
            // Log an error if the back button is null
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

    // Method to setup switch with a notificationType object.
    private void setupSwitch(Switch switchButton, NotificationType notificationType) {
        // If the switch's value isn't null
        if (switchButton != null) {
            // Set switchButton value to the notificationType's isEnabled value
            switchButton.setChecked(notificationType.getEnabled());
            // When switch change listener is activated (user switch input)
            // Change switch's isChecked value and change the notificationType's isEnabled value
            switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                notificationType.setEnabled(isChecked);
            });
        } else {
            Log.e("NotificationActivity", "Switch button is Null");
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 50;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        
        public boolean onFling(MotionEvent e1, MotionEvent e2) {
            float diffY = e2.getY() - e1.getY();
            VelocityTracker velocityTracker = VelocityTracker.obtain();
            velocityTracker.addMovement(e1);
            velocityTracker.addMovement(e2);
            velocityTracker.computeCurrentVelocity(1000); // pixels per second

            float velocityY = velocityTracker.getYVelocity();
            
            boolean result = false;

            //test for swipe down gesture
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    // Swipe down detected
                    // Navigate back to SettingsActivity
                    Intent intent = new Intent(NotificationActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish(); // Close the current activity
                    result = true;
                }
            }
            velocityTracker.recycle(); // Recycle the VelocityTracker
            return result;
        }
    }
    
    public boolean onDown(MotionEvent e) {
        return true; // Required to return true for fling to work
    }
}