package com.example.smishingdetectionapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    //define contants
    private static final String CHANNEL_ID = "YourChannelID";
    private static final String CHANNEL_NAME = "YourChannelName";
    private static final String CHANNEL_DESC = "YourChannelDescription";

    // context object to access system resources and services
    private Context mContext;

    // constructor to initialise context
    public NotificationHelper(Context context) {
        mContext = context;
    }

    //method to create a notificaiton
    public void createNotification(String title, String message) {
        //notification Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.hardhat_logo_notif)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        //get the system notification manager
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);
            notificationManager.createNotificationChannel(channel);
        }
        //send/build notification
        notificationManager.notify(0, builder.build());
    }
}