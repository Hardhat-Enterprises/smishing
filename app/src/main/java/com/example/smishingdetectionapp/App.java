package com.example.smishingdetectionapp;

import android.app.Application;

public class App extends Application {

    private static ConnectivityMonitor connectivityMonitor;

    @Override
    public void onCreate() {
        super.onCreate();
        connectivityMonitor = new ConnectivityMonitor(getApplicationContext());
    }

    public static ConnectivityMonitor getConnectivityMonitor() {
        return connectivityMonitor;
    }
}
