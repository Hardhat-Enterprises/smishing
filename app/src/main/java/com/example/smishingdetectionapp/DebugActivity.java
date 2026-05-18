package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.notifications.NotificationHelper;
import com.example.smishingdetectionapp.notifications.NotificationType;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.smishingdetectionapp.navigation.BottomNavCoordinator;

public class DebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        BottomNavCoordinator.setup(this, R.id.nav_settings);


        Button sms_pull_btn = findViewById(R.id.sms_pull_btn);

        sms_pull_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open sms activity
                startActivity(new Intent(DebugActivity.this, SmsActivity.class));
            }
        });

        Button basic_notif_btn = findViewById(R.id.basic_notif_btn);

        basic_notif_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create test notification
                NotificationType smishDetectionAlert = NotificationType.createSmishDetectionAlert(getApplicationContext());
                NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                notificationHelper.createNotification(smishDetectionAlert,"SMISHING MESSAGE TEST", "SMISHY MESSAGE");
            }
        });

        Button expand_notif_btn = findViewById(R.id.expand_notif_btn);

        expand_notif_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create test notification
                NotificationType spamDetectionAlert = NotificationType.createSpamDetectionAlert(getApplicationContext());
                NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                notificationHelper.createNotification(spamDetectionAlert,"SPAM MESSAGE TEST","SPAMMY MESSAGE");

            }
        });

        Button message_notif_btn = findViewById(R.id.message_notif_btn);
        message_notif_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create test notification
                NotificationType newsAlert = NotificationType.createNewsAlert(getApplicationContext());
                NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                notificationHelper.createNotification(newsAlert,"NEWS NOTIFICATION","SOME NEWS");
            }
        });

    }}