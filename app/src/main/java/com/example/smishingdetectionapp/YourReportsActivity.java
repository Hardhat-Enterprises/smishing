package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.smishingdetectionapp.news.SelectListener;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class YourReportsActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportlog);

    }

    // Set up Bottom Navigation
//    BottomNavigationView nav1 = findViewById(R.id.bottom_navigation);
//        nav1.setSelectedItemId(R.id.nav_reports); // Set the selected item for this activity
//        nav1.setOnItemSelectedListener(menuItem -> {
//        int id = menuItem.getItemId();
//        if (id == R.id.nav_home) {
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            overridePendingTransition(0, 0);
//            finish();
//            return true;
//        } else if (id == R.id.nav_news) {
//            startActivity(new Intent(getApplicationContext(), NewsActivity.class));
//            overridePendingTransition(0, 0);
//            finish();
//            return true;
//        } else if (id == R.id.nav_settings) {
//            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
//            overridePendingTransition(0, 0);
//            finish();
//            return true;
//        } else if (id == R.id.nav_reports) {
//            return true; // Current activity
//        }
//        return false;
//    });
}
