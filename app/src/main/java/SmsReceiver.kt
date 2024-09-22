package com.example.smishingdetectionapp

import com.example.smishingdetectionapp.AESUtil
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.io.IOException
import java.security.GeneralSecurityException

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("SmsReceiver", "onReceive called")

        // Ensure context is not null
        if (context == null) {
            Log.e("SmsReceiver", "Context is null, cannot proceed")
            return
        }

        // Check if the received intent is an SMS received action
        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            if (bundle != null) {
                Log.d("SmsReceiver", "Bundle is not null")
                val pdus = bundle.get("pdus") as Array<*>

                // Loop through all received PDUs (Protocol Data Units)
                for (pdu in pdus) {
                    val format = bundle.getString("format")
                    val smsMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        SmsMessage.createFromPdu(pdu as ByteArray, format)
                    } else {
                        SmsMessage.createFromPdu(pdu as ByteArray)
                    }

                    val messageBody = smsMessage.messageBody
                    val sender = smsMessage.originatingAddress

                    Log.d("SmsReceiver", "Received SMS: $messageBody from $sender")

                    // Log the original SMS content
                    Log.d("EncryptionUtil", "Original SMS content: $messageBody")

                    if (messageBody.isNullOrEmpty()) {
                        Log.e("SmsReceiver", "Message body is null or empty before encryption.")
                        return
                    }

                    try {
                        // Encrypt the SMS content
                        Log.d("AESUtil", "Starting encryption process")
                        val encryptedMessage = AESUtil.encrypt(messageBody)
                        if (encryptedMessage.isNullOrEmpty()) {
                            Log.e("SmsReceiver", "Failed to encrypt SMS content: encryptedMessage is null or empty.")
                        } else {
                            Log.d("AESUtil", "Encrypted SMS content: $encryptedMessage")
                        }

                        // Decrypt the SMS content when retrieving
                        Log.d("AESUtil", "Starting decryption process")

                        if (!encryptedMessage.isNullOrEmpty()) {
                            val decryptedMessage = AESUtil.decrypt(encryptedMessage)
                            Log.d("AESUtil", "Decrypted SMS content: $decryptedMessage")

                            // Display the decrypted SMS content with a Toast
                            Toast.makeText(context, "Decrypted SMS Content: $decryptedMessage", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("SmsReceiver", "Failed to decrypt SMS content: encryptedMessage is null or empty.")
                        }

                    } catch (e: GeneralSecurityException) {
                        Log.e("SmsReceiver", "Error encrypting or decrypting SMS content", e)
                    } catch (e: IOException) {
                        Log.e("SmsReceiver", "Error encrypting or decrypting SMS content", e)
                    }

                    // Check if the SMS message is suspected of smishing
                    if (isSmishingMessage(messageBody)) {
                        Log.d("SmsReceiver", "Smishing detected!")
                        sendNotification(context, sender, messageBody)
                    }
                }
            } else {
                Log.d("SmsReceiver", "Bundle is null")
            }
        }
    }

    // Method to determine if the message is a potential smishing attempt
    private fun isSmishingMessage(message: String): Boolean {
        return message.contains("suspicious link", true) || message.contains("urgent action required", true)
    }

    // Method to send a notification if smishing is detected
    private fun sendNotification(context: Context, sender: String?, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "smishing_channel",
                "Smishing Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, "smishing_channel")
            .setSmallIcon(R.drawable.ic_notification)  // Ensure this resource exists
            .setContentTitle("Smishing Alert!")
            .setContentText("Message from $sender: $message. DO NOT click on the link!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Display the notification
        notificationManager.notify(1, notification)
    }
}
