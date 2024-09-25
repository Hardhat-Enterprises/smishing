package com.example.smishingdetectionapp

import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


object KeyUtil {
    @Throws(Exception::class)
    fun generateAESKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256) // 256-bit key
        return keyGen.generateKey()
    }
}