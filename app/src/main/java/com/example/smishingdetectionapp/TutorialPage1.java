package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TutorialPage1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_page1); // Ensure this matches your XML layout file

        // Set up the Next button
        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            // Navigate to the second tutorial page
            Intent intent = new Intent(TutorialPage1.this, TutorialPage2.class);
            startActivity(intent);

        });
    }
}
