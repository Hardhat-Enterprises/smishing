package com.example.smishingdetectionapp

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.IOException
import java.security.GeneralSecurityException

object EncryptionUtil {
    // Make this method static
    @Throws(GeneralSecurityException::class, IOException::class)
    fun getEncryptedSharedPreferences(context: Context?): SharedPreferences {
        val masterKey = MasterKey.Builder(context!!)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "encrypted_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}