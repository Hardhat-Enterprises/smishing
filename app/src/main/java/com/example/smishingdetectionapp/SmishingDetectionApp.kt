package com.example.smishingdetectionapp

import android.app.Application
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate

class SmishingDetectionApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val darkModeEnabled = prefs.getBoolean("dark_mode", false) // default = light

        AppCompatDelegate.setDefaultNightMode(
            if (darkModeEnabled)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}