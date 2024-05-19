package com.example.smishingdetectionapp;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREF_NAME = "user_preferences";
    private static final String KEY_FONT_SIZE = "font_size";

    // Initialize default font size if not set
    public static void init(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (!prefs.contains(KEY_FONT_SIZE)) {
            setFontSize(context, context.getResources().getDimension(R.dimen.text_size_multiplier));
        }
    }

    public static void setFontSize(Context context, float fontSize) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(KEY_FONT_SIZE, fontSize);
        editor.apply();
    }

    public static float getFontSize(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getFloat(KEY_FONT_SIZE, context.getResources().getDimension(R.dimen.text_size_multiplier));
    }
}
