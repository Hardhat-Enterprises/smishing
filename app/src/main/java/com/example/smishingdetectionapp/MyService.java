package com.example.smishingdetectionapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {



//    private static final String TAG = "98888";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AccessControl accessControl = new AccessControl();
        String userId = "123456"; // 현재 사용자 ID
        String requiredPermission = "START_BACKGROUND_SERVICE";

        if (accessControl.hasPermission(userId, requiredPermission)) {
            // 권한이 있을 경우 서비스 실행
            Log.d("MyService", "서비스 시작: 권한이");
        } else {
            // 권한이 없을 경우 서비스 종료
            Log.d("MyService", "서비스 종료: 권한이.");
            stopSelf(); // 서비스 종료
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}