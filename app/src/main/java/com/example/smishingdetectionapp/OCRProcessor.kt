package com.example.smishingdetectionapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.cloud.vision.v1.ImageAnnotatorSettings
import com.google.protobuf.ByteString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class OCRProcessor(private val context: Context) {

    private lateinit var visionClient: ImageAnnotatorClient
    private lateinit var translateClient: Translate

    init {
        initializeClients()
    }

    // Initialise Google Cloud Vision and Translation clients
    private fun initializeClients() {
        try {
            val credentialsStream: InputStream = context.resources.openRawResource(R.raw.googlecredentialsocr)
            val credentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
            val imageAnnotatorSettings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build()
            visionClient = ImageAnnotatorClient.create(imageAnnotatorSettings)
            val translateOptions = TranslateOptions.newBuilder().setCredentials(credentials).build()
            translateClient = translateOptions.service
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Suspend function to process an image and translate detected text
    suspend fun processImage(imageUri: Uri, targetLanguage: String, callback: OCRCallback) {
        val result = try {
            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri))
            val imgBytes = ByteString.copyFrom(bitmapToByteArray(bitmap))
            val img = Image.newBuilder().setContent(imgBytes).build()
            val request = AnnotateImageRequest.newBuilder()
                .addFeatures(Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build())
                .setImage(img)
                .build()

            val requests = listOf(request)

            // Perform the OCR request
            val response = visionClient.batchAnnotateImages(requests).responsesList[0]
            if (response.hasError()) {
                "Error: ${response.error.message}"
            } else {
                val extractedText = response.textAnnotationsList.joinToString("\n") { it.description }
                translateText(extractedText, targetLanguage)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            "Error: ${e.message}"
        }

        withContext(Dispatchers.Main) {
            callback.onResult(result)
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    // Translate text using Google Cloud Translation API
    private suspend fun translateText(text: String, targetLanguage: String): String {
        return withContext(Dispatchers.IO) {
            val translation = translateClient.translate(
                text,
                Translate.TranslateOption.targetLanguage(targetLanguage)
            )
            translation.translatedText
        }
    }

    interface OCRCallback {
        fun onResult(result: String)
    }
}





