package com.example.smishingdetectionapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import androidx.core.app.NotificationCompat

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle.get("pdus") as Array<*>
                for (pdu in pdus) {
                    val format = bundle.getString("format")
                    val smsMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        SmsMessage.createFromPdu(pdu as ByteArray, format)
                    } else {
                        SmsMessage.createFromPdu(pdu as ByteArray)
                    }
                    val messageBody = smsMessage.messageBody
                    val sender = smsMessage.originatingAddress

                    if (isSmishingMessage(messageBody)) {
                        sendNotification(context, sender, messageBody)
                    }
                }
            }
        }
    }

    private fun isSmishingMessage(message: String): Boolean {
        // Implement your smishing detection logic here
        return message.contains("suspicious link") || message.contains("urgent action required")
    }

    private fun sendNotification(context: Context?, sender: String?, message: String) {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("smishing_channel", "Smishing Alerts", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "smishing_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("DO NOT click on the link")
            .setContentText("Suspicious message from $sender: $message")
            .setStyle(NotificationCompat.BigTextStyle().bigText("DO NOT click on the link.\nSuspicious message from $sender: $message"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    }