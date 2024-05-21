package com.example.smishingdetectionapp;

import android.app.Application;
import android.util.TypedValue;
import android.widget.TextView;

public class AppPreferences extends Application {

    private static AppPreferences instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static AppPreferences getInstance() {
        return instance;
    }

    public static void applyFontSize(TextView textView) {
        float fontSize = PreferenceManager.getFontSize(instance);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
    }
}
