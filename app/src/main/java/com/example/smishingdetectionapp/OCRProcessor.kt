package com.example.smishingdetectionapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.protobuf.ByteString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

// Class responsible for handling OCR and text translation
class OCRProcessor(private val context: Context) {

    // Google Cloud Vision client for OCR
    private lateinit var visionClient: ImageAnnotatorClient

    // Google Cloud Translation client for translating text
    private lateinit var translateClient: Translate

    init {
        // Initialize the Google Cloud clients
        initializeClients()
    }

    // Function to initialize the Google Cloud Vision and Translation clients
    private fun initializeClients() {
        try {
            // Load the JSON key file from the raw resources directory
            val credentialsStream: InputStream = context.resources.openRawResource(R.raw.googlecredentialsocr)
            val credentials = GoogleCredentials.fromStream(credentialsStream)

            // Initialize the Vision API client
            visionClient = ImageAnnotatorClient.create()

            // Initialize the Translation API client
            val translateOptions = TranslateOptions.newBuilder().setCredentials(credentials).build()
            translateClient = translateOptions.service
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Function to process an image URI and translate the detected text
    fun processImage(imageUri: Uri, targetLanguage: String, callback: OCRCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                // Decode the image URI into a Bitmap
                val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri))
                // Convert the Bitmap to a ByteString
                val imgBytes = ByteString.copyFrom(bitmapToByteArray(bitmap))

                // Build the image request for the Vision API
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
                    // Extract the detected text from the response
                    val extractedText = StringBuilder()
                    for (annotation in response.textAnnotationsList) {
                        extractedText.append(annotation.description)
                    }

                    // Translate the extracted text
                    translateText(extractedText.toString(), targetLanguage)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                "Error: ${e.message}"
            }
            withContext(Dispatchers.Main) {
                callback.onResult(result)
            }
        }
    }

    // Function to convert a Bitmap to a ByteArray
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    // Function to translate text using the Google Cloud Translation API
    private fun translateText(text: String, targetLanguage: String): String {
        val translation = translateClient.translate(
            text,
            Translate.TranslateOption.targetLanguage(targetLanguage)
        )
        return translation.translatedText
    }

    // Interface for callback to handle the result
    interface OCRCallback {
        fun onResult(result: String)
    }
}
