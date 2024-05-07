package com.example.smishingdetectionapp.notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.smishingdetectionapp.R;

public class NotificationHelper {

    // context object to access system resources and services
    private Context mContext;

    // constructor to initialise context - context is used to access system services
    public NotificationHelper(Context context) {
        mContext = context;
    }

    // method to create and display a notification
    // parameters include NotificationType Object which includes different attributes for different types of notifications.
    // different attributes for different types of notifications which changes how the createNotification method creates/displays the notifications
    public void createNotification(NotificationType notif_type, String title, String message)
    {
        // generate a unique ID for the created notification - this is so that notifications arent overwritten by new notifications
        int notificationID = generateNotificationID();
        // get the system's NotificationManager service to manage notifications
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //PendingIntent dismissPendingIntent = NotificationActionReceiver.createDismissIntent(mContext, notificationID);

        // create notification channel if it doesnt exist
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notif_type, notificationManager);
        }

        // notification builder - uses provided parameters and context to build notification content
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, notif_type.getChannelID())
                .setSmallIcon(R.drawable.hardhat_logo_notif)
                .setContentTitle(title)
                .setContentText(message)
                //.addAction(R.drawable.close_button,"Dismiss", dismissPendingIntent)
                .setPriority(notif_type.getImportance());

        // if the message is longer than 100 characters, use the expanded style notification
        if (message.length() > 100) {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        }

        // post the notification
        notificationManager.notify(notificationID, builder.build());
    }

    // method to create a notification channel if it doesnt already exist
    private void createNotificationChannel(NotificationType type, NotificationManager notificationManager)
    {
        // check if the channel already exists to avoid re-creating the channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(type.getChannelID()) == null) {
                // create new NotificationChannel object with the given parameters
                // from the NotificationType object
                @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(
                        type.getChannelID(),
                        type.getChannelName(),
                        type.getImportance()
                );
                // set description of channel and register the channel with the system
                channel.setDescription(type.getChannelDesc());
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    // generate a unique notification ID based on the current time in milliseconds
    private int generateNotificationID()
    {
        return (int) System.currentTimeMillis();
    }

}

