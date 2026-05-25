package com.example.smishingdetectionapp;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.example.smishingdetectionapp.ui.onboarding.OnboardingActivity;
import com.example.smishingdetectionapp.MainActivity;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // Splash screen duration in milliseconds
    private ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for the splash screen
        setContentView(R.layout.activity_splash_screen);

        // Initialize the ImageView
        imageview = findViewById(R.id.imageview);

        // Log splash animation start
        Log.d("SplashScreen", "Splash screen animation started.");

        // Start splash animation
        ObjectAnimator animator = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.smishing_detection_logo_animator);
        animator.setTarget(imageview);  // Set the animation target to the imageview
        animator.start();  // Start the animation

        // Handle the onboarding flow
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean onboardingShown = prefs.getBoolean("onboarding_shown", false);

        Intent intent;

        if (!onboardingShown) {
            // First-time user -> Show OnboardingActivity
            intent = new Intent(SplashScreen.this, OnboardingActivity.class);
            prefs.edit().putBoolean("onboarding_shown", true).apply();  // Mark onboarding as completed

            // Log the navigation to OnboardingActivity
            Log.d("SplashScreen", "Navigating to OnboardingActivity.");
        } else {
            // Returning user -> Show MainActivity or LoginActivity
            intent = new Intent(SplashScreen.this, MainActivity.class);  // Or LoginActivity if preferred

            // Log the navigation to MainActivity
            Log.d("SplashScreen", "Navigating to MainActivity.");
        }

        // Start the appropriate activity
        startActivity(intent);

        // Log the end of the splash screen
        Log.d("SplashScreen", "Splash screen finished. Moving to the next activity.");

        finish();  // Close SplashScreen
    }
}