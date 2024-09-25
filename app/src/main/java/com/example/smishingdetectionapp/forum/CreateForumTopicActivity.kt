package com.example.smishingdetectionapp.forum

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smishingdetectionapp.DatabaseAccess
import com.example.smishingdetectionapp.R

class CreateForumTopicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_forum_topic)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the back button to go back to the previous screen
        val report_back = findViewById<ImageButton>(R.id.forum_back_button)
        report_back.setOnClickListener {
            finish()
        }

        // Initializing input fields and submit buttons
        val topicTitleInput = findViewById<EditText>(R.id.input_title)
        val topicDescriptionInput = findViewById<EditText>(R.id.input_description)
        val submitButton = findViewById<Button>(R.id.submit_button)

        // Submit button functionality
        submitButton.setOnClickListener {
            val title = topicTitleInput.text.toString().trim()
            val description = topicDescriptionInput.text.toString().trim()

            if (title.isEmpty()) {
                showToast("Please enter a title for the topic.")
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                showToast("Please enter a description for the topic.")
                return@setOnClickListener
            }

            val isSubmitted = DatabaseAccess.submitForumTopic(title, description)
            if (isSubmitted) {
                showToast("Topic submitted successfully!")
                finish()
            } else {
                showToast("Failed to submit topic. Please try again.")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}