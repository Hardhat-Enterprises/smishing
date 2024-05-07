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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NotificationActivity extends AppCompatActivity {

    private Switch newsSwitch;
    private Switch smishingSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the layout defined in 'activity_notification.xml'
        setContentView(R.layout.activity_notification);
        // Enable edge-to-edge display to allow your layout to extend into the window insets
        EdgeToEdge.enable(this);

        // Initialize the Switch objects from the layout
        newsSwitch = findViewById(R.id.news_notification_switch);
        smishingSwitch = findViewById(R.id.smishing_notification_switch);

        // Check if both switches are successfully initialized to avoid NullPointerException
        if (newsSwitch != null && smishingSwitch != null) {
            // Load saved states for the switches from SharedPreferences
            loadSwitchStates();

            // Set up a listener for the news switch to save its state when toggled
            newsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                saveSwitchState("news", isChecked);
            });

            // Set up a listener for the smishing switch to save its state when toggled
            smishingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                saveSwitchState("smishing", isChecked);
            });
        } else {
            // Log an error if one of the switches is not properly initialized
            Log.e("NotificationActivity", "One of the switches is null.");
        }

        // Additional UI setup
        // Initialize the main view container object from the layout
        View mainView = findViewById(R.id.notification_main);
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

    // This method is used to load the saved states of the switches from SharedPreferences.
    // It sets the switches to the state that was saved last time the app was used.
    private void loadSwitchStates() {
        // Get the SharedPreferences object for 'settings' where switch states are stored.
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        newsSwitch.setChecked(prefs.getBoolean("news", false));
        smishingSwitch.setChecked(prefs.getBoolean("smishing", false));
    }

    // This method saves the state of a given switch to SharedPreferences.
    // 'key' is the name of the preference to modify, and 'state' is the new value for the preference.
    private void saveSwitchState(String key, boolean state) {
        // Get the SharedPreferences object for 'settings'.
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        // Get the SharedPreferences.Editor object, which allows modifications to the data.
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, state);
        editor.apply();
    }

}

