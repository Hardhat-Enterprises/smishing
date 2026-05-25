package com.example.smishingdetectionapp.Community;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.NewsActivity;
import com.example.smishingdetectionapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.SettingsActivity;

public class CommunityReportActivity extends AppCompatActivity {

    private String selectedCategory = "Smishing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_report);

        EditText etPhone = findViewById(R.id.etPhoneNumber);
        EditText etMessage = findViewById(R.id.etMessageContent);
        Button btnReport = findViewById(R.id.btnReportProtect);
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);

        final String source =
                (getIntent().getStringExtra("source") == null
                        ? "home"
                        : getIntent().getStringExtra("source"));

        // Set up category spinner
        String[] categories = {"Smishing", "Phishing", "Spam", "Scam Call", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectedCategory = categories[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "Smishing";
            }
        });

        // Tabs
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Trending"));
        tabLayout.addTab(tabLayout.newTab().setText("Posts"));
        tabLayout.addTab(tabLayout.newTab().setText("Report"));
        tabLayout.getTabAt(2).select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if (pos == 0) {
                    Intent i = new Intent(CommunityReportActivity.this,
                            CommunityHomeActivity.class);
                    i.putExtra("source", source);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    finish();
                } else if (pos == 1) {
                    Intent i = new Intent(CommunityReportActivity.this,
                            CommunityPostActivity.class);
                    i.putExtra("source", source);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });

        // Back button — navigate only, no validation here
        ImageButton community_back = findViewById(R.id.community_back);
        if (community_back != null) {
            community_back.setOnClickListener(view -> {
                if ("settings".equals(source)) {
                    startActivity(new Intent(this, SettingsActivity.class));
                    overridePendingTransition(0, 0);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    overridePendingTransition(0, 0);
                }
                finish();
            });
        } else {
            Log.e("CommunityReportActivity", "Back button is null");
        }

        // Bottom navigation
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_report);
        nav.setOnItemSelectedListener(item -> {
            Intent intent;
            int id = item.getItemId();
            if      (id == R.id.nav_home)     intent = new Intent(this, MainActivity.class);
            else if (id == R.id.nav_news)     intent = new Intent(this, NewsActivity.class);
            else if (id == R.id.nav_settings) intent = new Intent(this, SettingsActivity.class);
            else return false;
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
            return true;
        });

        // Submit button with Australian phone validation
        btnReport.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String msg = etMessage.getText().toString().trim();

            if (phone.isEmpty() || msg.isEmpty()) {
                Toast.makeText(this, "Please help us complete this",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Strip spaces, dashes, brackets for validation
            String digitsOnly = phone.replaceAll("[\\s\\-().+]", "");

            boolean isAustralian = digitsOnly.matches("04\\d{8}") ||
                    digitsOnly.matches("0[2378]\\d{8}") ||
                    digitsOnly.matches("614\\d{8}");

            if (!isAustralian) {
                Toast.makeText(this,
                        "Please enter a valid Australian phone number up to 10 characters",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            CommunityDatabaseAccess dbAccess = new CommunityDatabaseAccess(this);
            dbAccess.open();
            dbAccess.insertOrUpdateReport(phone, msg);
            dbAccess.close();

            Toast.makeText(this,
                    "Report submitted as \"" + selectedCategory + "\". Thank you!",
                    Toast.LENGTH_LONG).show();
            etPhone.setText("");
            etMessage.setText("");
            spinnerCategory.setSelection(0);
        });
    }
}