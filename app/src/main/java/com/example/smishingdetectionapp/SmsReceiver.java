//package com.example.smishingdetectionapp;
//import android.Manifest;
//
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.telephony.SmsMessage;
//import android.util.Log;
//
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//import androidx.core.content.ContextCompat;
//
//public class SmsReceiver extends BroadcastReceiver {
//    private static final String TAG = "SmsReceiver";
//    private static final String CHANNEL_ID = "smishing_alert_channel";
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
//                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
//                Log.e(TAG, "SMS permissions are not granted");
//                return;
//            }
//
//            SmsMessage[] messages = extractSmsMessages(intent);
//            if (messages != null) {
//                for (SmsMessage sms : messages) {
//                    if (sms != null) {
//                        String sender = sms.getDisplayOriginatingAddress();
//                        String messageBody = sms.getMessageBody();
//
//                        if (sender != null && messageBody != null) {
//                            boolean isSuspicious = SmishingDetector.isSmishingMessage(messageBody.toLowerCase());
//                            if (isSuspicious) {
//                                showNotification(context, sender, messageBody);
//                            }
//                        } else {
//                            Log.e(TAG, "Sender or message body is null");
//                        }
//                    } else {
//                        Log.e(TAG, "SmsMessage is null");
//                    }
//                }
//            } else {
//                Log.e(TAG, "No messages found");
//            }
//        }
//    }
//
//    private SmsMessage[] extractSmsMessages(Intent intent) {
//        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
//        if (pdus == null) {
//            Log.e(TAG, "No PDUs found in the intent");
//            return null;
//        }
//
//        SmsMessage[] messages = new SmsMessage[pdus.length];
//        String format = intent.getStringExtra("format");
//        if (format == null) {
//            format = "3gpp"; // Default format
//        }
//
//        for (int i = 0; i < pdus.length; i++) {
//            try {
//                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
//            } catch (Exception e) {
//                Log.e(TAG, "Error creating SmsMessage from PDU", e);
//            }
//        }
//
//        return messages;
//    }
//
//    private void showNotification(Context context, String sender, String messageBody) {
//        Intent intent = new Intent(context, SMSMessageDetailActivity.class);
//        intent.putExtra("SMS_SENDER", sender);
//        intent.putExtra("SMS_BODY", messageBody);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.hardhat_logo)
//                .setContentTitle("Suspicious Message Detected")
//                .setContentText("Possible phishing message from " + sender)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
//            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
//        }
//    }
//}

package com.example.smishingdetectionapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    private static final String CHANNEL_ID = "smishing_alert_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            // Check SMS permissions
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "SMS permissions are not granted");
                return;
            }

            SmsMessage[] messages = extractSmsMessages(intent);
            if (messages != null) {
                for (SmsMessage sms : messages) {
                    if (sms != null) {
                        String sender = sms.getDisplayOriginatingAddress();
                        String messageBody = sms.getMessageBody();

                        if (sender != null && messageBody != null) {
                            boolean isSuspicious = SmishingDetector.isSmishingMessage(messageBody.toLowerCase());
                            if (isSuspicious) {
                                showNotification(context, sender, messageBody);
                            }
                        } else {
                            Log.e(TAG, "Sender or message body is null");
                        }
                    } else {
                        Log.e(TAG, "SmsMessage is null");
                    }
                }
            } else {
                Log.e(TAG, "No messages found");
            }
        }
    }

    private SmsMessage[] extractSmsMessages(Intent intent) {
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        if (pdus == null) {
            Log.e(TAG, "No PDUs found in the intent");
            return null;
        }

        SmsMessage[] messages = new SmsMessage[pdus.length];
        String format = intent.getStringExtra("format");
        for (int i = 0; i < pdus.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
        }
        return messages;
    }

    private void showNotification(Context context, String sender, String messageBody) {
        Intent intent = new Intent(context, SMSMessageDetailActivity.class);
        intent.putExtra("SMS_SENDER", sender);
        intent.putExtra("SMS_BODY", messageBody);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.hardhat_logo)
                .setContentTitle("Suspicious Message Detected")
                .setContentText("Possible phishing message from " + sender)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
