package com.example.smishingdetectionapp

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

fun encryptAES(message: String, secretKey: SecretKey, iv: ByteArray): ByteArray {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
    return cipher.doFinal(message.toByteArray())
}

fun encryptAESKeyWithRSA(secretKey: SecretKey, publicKey: PublicKey): ByteArray {
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    return cipher.doFinal(secretKey.encoded)
}
