package com.example.smishingdetectionapp;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.DataBase.UserResponse;
import com.example.smishingdetectionapp.news.NewsAdapter;

import com.example.smishingdetectionapp.ui.account.AccountActivity;
import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingsActivity extends AppCompatActivity {

    private TextView emailTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize TextView
        emailTextView = findViewById(R.id.email);

        // Retrieve JWT token from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token != null) {
            // Send the JWT token to the backend to fetch the email
            fetchUserEmail(token);
        } else {
            // Handle case where no JWT is found (e.g., user is logged out)
            emailTextView.setText("No token found. Please log in.");
        }

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
            } else if (id == R.id.nav_settings) {
                return true;
            }

            return false;
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


        private void fetchUserEmail(String token) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.SERVERIP)  // Set your backend URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Retrofitinterface apiService = retrofit.create(Retrofitinterface.class);

            // Send JWT token in the Authorization header to get the user's email
            Call<UserResponse> call = apiService.getUserDetails("Bearer " + token);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful()) {
                        // Fetch the user's email from the response
                        String userEmail = response.body().getEmail();
                        emailTextView.setText(userEmail);
                    } else {
                        emailTextView.setText("Failed to fetch email");
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    emailTextView.setText("Error: " + t.getMessage());
                }
            });
        }


    //Notification button to switch to notification page
    public void openNotificationsActivity(View view) {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }




}

