package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.notifications.NotificationHelper;
import com.example.smishingdetectionapp.notifications.NotificationType;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DebugActivity extends AppCompatActivity {

    BottomNavigationView nav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        /*if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_settings, new SettingsActivity())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }*/

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);

        nav.setSelectedItemId(R.id.nav_settings);

        nav.setOnItemSelectedListener(menuItem -> {

            int id = menuItem.getItemId();
            if(id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            }
            else if(id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                finish();
                return true;
            }
            else if(id == R.id.nav_settings){
                return true;
            }

            return false;
        });

        Button basic_notif_btn = findViewById(R.id.basic_notif_btn);

        basic_notif_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create test notification
                NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                notificationHelper.createNotification(NotificationType.DETECTION_ALERT,"basic_notification_test","short message");
            }
        });

        Button expand_notif_btn = findViewById(R.id.expand_notif_btn);

        expand_notif_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create test notification
                NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                notificationHelper.createNotification(NotificationType.DETECTION_ALERT,"expand_notification_test","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
            }
        });

        Button message_notif_btn = findViewById(R.id.message_notif_btn);
        message_notif_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create test notification
                NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                notificationHelper.createNotification(NotificationType.NEWS_UPDATE,"different_channel_test","short message");
            }
        });

   /* class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }*/
    }}