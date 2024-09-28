package com.example.smishingdetectionapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    //    private static final String TAG = "";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AccessControl accessControl = new AccessControl();
        String userId = "kimdongh@deakin.edu.au";
        String requiredPermission = "START_BACKGROUND_SERVICE";

        if (accessControl.hasPermission(userId, requiredPermission)) {

            Log.d("MyService", "Service Start: you have permission ");
        } else {

            Log.d("MyService", "Service end: you don't have permission.");
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}