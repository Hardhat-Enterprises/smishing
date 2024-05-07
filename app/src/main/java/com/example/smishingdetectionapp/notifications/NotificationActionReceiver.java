package com.example.smishingdetectionapp.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        int notificationID = intent.getIntExtra("notificationID", 0);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationID);

    }

    public static PendingIntent createDismissIntent(Context context, int notificationID) {
        Intent dismissIntent = new Intent(context, NotificationActionReceiver.class);
        dismissIntent.putExtra("notification_id", notificationID);
        return PendingIntent.getBroadcast(context, notificationID, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

}