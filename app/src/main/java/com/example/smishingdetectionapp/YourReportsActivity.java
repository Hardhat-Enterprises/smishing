package com.example.smishingdetectionapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.detections.ReportsAdapter;
import com.example.smishingdetectionapp.news.SelectListener;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class YourReportsActivity extends AppCompatActivity {

    private RecyclerView reportsRecyclerView;
    private DatabaseAccess databaseAccess;
    private ReportsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportlog);

        ImageButton backbtn = findViewById(R.id.report_back);
        backbtn.setOnClickListener(v -> {
            finish();
        });

        // Initialize RecyclerView and database access
        reportsRecyclerView = findViewById(R.id.lvSavedReportsList);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        // Load reports
        loadReports();


//
//    // Navigate back to Settings Page
//    ImageButton reportLogBack = findViewById(R.id.report_back);
//        reportLogBack.(v ->
//
//    {
//
//        Intent intent = new Intent(YourReportsActivity.this, SettingsActivity.class);
//        startActivity(intent);
//        finish();
//         Optional: Finish the current activity
//        });
//
//         Set up Bottom Navigation
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
//        Button to access the Log of Reports upon Click

    }

    private void loadReports() {
        try {
            Cursor cursor = databaseAccess.getReports();
            if (cursor != null) {
                adapter = new ReportsAdapter(this, cursor);
                reportsRecyclerView.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading reports", Toast.LENGTH_SHORT).show();
        }
    }

}
