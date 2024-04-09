package com.example.smishingdetectionapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class NotificationHelper {
    //define contants
    private static final String CHANNEL_ID = "ChannelID";
    private static final String CHANNEL_NAME = "ChannelName";
    private static final String CHANNEL_DESC = "ChannelDescription";

    // context object to access system resources and services
    private Context mContext;

    // constructor to initialise context
    public NotificationHelper(Context context) {
        mContext = context;
    }

    //method to create a notification

    //basic notif override
    public void createNotification(String title) {
        createNotification(title, null, null);
    }

    public void createNotification(String title, String message) {
        createNotification(title, message, null);
    }

    public void createNotification(String title, String message, String message_details) {

        //**creates an intent for the notification**
        //Intent intent = new Intent(this, AlertDetails.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        //**notification Builder**
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.hardhat_logo_notif)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message_details))
                .setGroup(CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //.setContentIntent(pendingIntent)

        //get the system notification manager
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESC);
            notificationManager.createNotificationChannel(channel);
        }
        //send/build notification
        notificationManager.notify(0, builder.build());
    }
}