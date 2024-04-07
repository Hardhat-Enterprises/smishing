package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class NewsActivity extends AppCompatActivity {

    BottomNavigationView nav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_navigation), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);

        nav.setSelectedItemId(R.id.nav_news);

        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();
                if(id == R.id.nav_home){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                else if(id == R.id.nav_news) {
                    return true;
                }
                else if(id == R.id.nav_settings){
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }

                return false;
            }

    });
}}