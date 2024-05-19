package com.example.smishingdetectionapp;

import android.app.Application;
import android.content.Context;
import android.util.TypedValue;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

public class MyApp extends Application {

    private static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApp getInstance() {
        return instance;
    }

    public static void applyFontSize(TextView textView) {
        float fontSize = PreferenceManager.getFontSize(instance);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
    }
}
