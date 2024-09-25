package com.example.smishingdetectionapp

import android.util.Base64
import android.util.Log
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AESUtil {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
    private const val ENCRYPTION_KEY = "16CharSecretKey!"  // Must be 16 chars for 128-bit encryption

    // Encrypt the data
    fun encrypt(data: String): String? {
        Log.d("AESUtil", "Starting encryption process")

        return try {
            val key: Key = generateKey()
            Log.d("AESUtil", "Generated encryption key: $key")

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val encryptedBytes = cipher.doFinal(data.toByteArray())

            val encryptedString = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
            Log.d("AESUtil", "Successfully encrypted data: $encryptedString")

            encryptedString
        } catch (e: Exception) {
            Log.e("AESUtil", "Error during encryption", e)
            null
        }
    }

    // Decrypt the data
    fun decrypt(encryptedData: String): String? {
        Log.d("AESUtil", "Starting decryption process")

        return try {
            val key: Key = generateKey()
            Log.d("AESUtil", "Generated decryption key: $key")

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, key)
            val decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(decodedBytes)

            val decryptedString = String(decryptedBytes)
            Log.d("AESUtil", "Successfully decrypted data: $decryptedString")

            decryptedString
        } catch (e: Exception) {
            Log.e("AESUtil", "Error during decryption", e)
            null
        }
    }

    // Generate AES Key
    private fun generateKey(): Key {
        return SecretKeySpec(ENCRYPTION_KEY.toByteArray(), ALGORITHM)
    }
}
