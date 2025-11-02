package com.example.smishingdetectionapp;

import android.app.Application;

// IMPORT the ConnectivityMonitor from its package
import com.example.smishingdetectionapp.Connectivity.ConnectivityMonitor;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Call the static init on the Kotlin object (no "new")
        ConnectivityMonitor.init(getApplicationContext());
        // If your Kotlin object does NOT have @JvmStatic on init(),
        // use: ConnectivityMonitor.INSTANCE.init(getApplicationContext());
    }
}
