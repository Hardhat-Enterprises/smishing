package com.example.smishingdetectionapp

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OCRActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var textViewExtractedText: TextView
    private lateinit var textViewTranslatedText: TextView
    private lateinit var ocrProcessor: OCRProcessor
    private val getContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            processSelectedImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)

        imageView = findViewById(R.id.imageView)
        textViewExtractedText = findViewById(R.id.textViewExtractedText)
        textViewTranslatedText = findViewById(R.id.textViewTranslatedText)
        ocrProcessor = OCRProcessor(this)

        val buttonProcess: Button = findViewById(R.id.buttonProcess)
        buttonProcess.setOnClickListener {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        getContent.launch("image/*")
    }

    // Process the selected image and perform OCR and translation
    private fun processSelectedImage(imageUri: Uri) {
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
        imageView.setImageBitmap(bitmap)

        lifecycleScope.launch(Dispatchers.IO) {
            ocrProcessor.processImage(imageUri, "es", object : OCRProcessor.OCRCallback {
                override fun onResult(result: String) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        if (result.startsWith("Error")) {
                            Toast.makeText(this@OCRActivity, result, Toast.LENGTH_SHORT).show()
                        } else {
                            textViewExtractedText.text = result
                        }
                    }
                }
            })
        }
    }
}



