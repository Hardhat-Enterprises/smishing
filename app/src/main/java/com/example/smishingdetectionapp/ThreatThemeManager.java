package com.example.smishingdetectionapp;

import android.content.Context;
import android.content.SharedPreferences;

public class ThreatThemeManager {

    private static final String PREFS_NAME = "threat_preferences";
    private static final String KEY_THREAT_LEVEL = "current_threat_level";

    public int getDrawableForLevel(Context context, int threatLevel) {
        switch (threatLevel) {
            case 0: // SAFE
                return R.drawable.counter_buttons_safe;
            case 1: // CAUTION
                return R.drawable.counter_buttons_caution;
            case 2: // ALERT
                return R.drawable.counter_buttons_alert;
            default:
                return R.drawable.counter_buttons_safe;
        }
    }

    public void persistThreatLevel(Context context, int threatLevel) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putInt(KEY_THREAT_LEVEL, threatLevel)
                .apply();
    }

    public int getPersistedThreatLevel(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THREAT_LEVEL, 0); // Default to SAFE if not found
    }
}