package com.example.smishingdetectionapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public final class SessionManager {
    private static final String PREFS = "user_session";
    private static final String KEY_EMAIL = "logged_in_email";

    private SessionManager() {}

    public static void saveEmail(Context context, String email) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_EMAIL, email).apply();
    }

    public static String getEmail(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sp.getString(KEY_EMAIL, null);
    }

    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }
}
