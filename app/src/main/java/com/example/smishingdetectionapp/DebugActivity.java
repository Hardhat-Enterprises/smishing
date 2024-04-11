package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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
                notificationHelper.createNotification("Basic Title", "Basic Message");
            }
        });

        Button expand_notif_btn = findViewById(R.id.expand_notif_btn);

        expand_notif_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create test notification
                NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                notificationHelper.createNotification("Expand Test", "Details", "Notifications provide short, timely information about events in your app while it isn't in use. This document shows you how to create a notification with various features. For an introduction to how notifications appear on Android, see the Notifications overview. For sample code that uses notifications, see the People sample on GitHub.");
            }
        });

        Button message_notif_btn = findViewById(R.id.message_notif_btn);
        message_notif_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create test notification
                NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                notificationHelper.createNotification("Test Title");
            }
        });

   /* class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }*/
    }}