package com.example.smishingdetectionapp
import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast

fun sendEncryptedSms(context: Context, phoneNumber: String, message: String) {
    try {
        // Encrypt the message using AES
        val encryptedMessage = AESUtil.encrypt(message)

        // Send the encrypted SMS using SmsManager
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, encryptedMessage, null, null)

        Log.d("SmsSender", "Encrypted message sent: $encryptedMessage")
        Toast.makeText(context, "Encrypted message sent: $encryptedMessage", Toast.LENGTH_SHORT).show()

    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("SmsSender", "Error sending encrypted SMS", e)
    }
}
