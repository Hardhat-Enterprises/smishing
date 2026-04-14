package com.example.smishingdetectionapp;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatDelegate;

public class SmishingDetectionApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkModeEnabled = prefs.getBoolean("dark_mode", false); // default = light

        AppCompatDelegate.setDefaultNightMode(
                darkModeEnabled
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

}
