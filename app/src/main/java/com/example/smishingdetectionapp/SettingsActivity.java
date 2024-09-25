package com.example.smishingdetectionapp;

import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.smishingdetectionapp.ui.account.AccountActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);

        nav.setSelectedItemId(R.id.nav_settings);

        nav.setOnItemSelectedListener(menuItem -> {

            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            } else return id == R.id.nav_settings;
        });

        //Account button to switch to account page
        Button accountBtn = findViewById(R.id.accountBtn);
        accountBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, AccountActivity.class));
            finish();
        });
        //Filtering button to switch to Smishing rules page
        Button filteringBtn = findViewById(R.id.filteringBtn);
        filteringBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, SmishingRulesActivity.class));
            finish();
        });

        //Report button to switch to reporting page
        Button reportBtn = findViewById(R.id.reportBtn);
        reportBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ReportingActivity.class));
            finish();
        });

        //Help button to switch to Help page
        Button helpBtn = findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, HelpActivity.class));
            finish();
        });

        //Feedback Button to switch to Feedback page
        Button feedbackBtn = findViewById(R.id.feedbackBtn);
        feedbackBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, FeedbackActivity.class));
            finish();
        });

        //Forum Button to switch to Forum page
        Button forumBtn = findViewById(R.id.forumBtn);
        forumBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ForumActivity.class));
        });

        //SDBot Button to switch to SDBot page
        Button SDBotBtn = findViewById(R.id.SDBotBtn);
        SDBotBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, SDBotActivity.class));
        });


        Button aboutMeButton = findViewById(R.id.aboutMeBtn);
        aboutMeButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, AboutMeActivity.class);
            startActivity(intent);
        });
    }

    //Notification button to switch to notification page
    public void openNotificationsActivity(View view) {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }
}

