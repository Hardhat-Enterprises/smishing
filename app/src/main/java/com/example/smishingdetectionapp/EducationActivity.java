package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import com.example.smishingdetectionapp.ui.CaseStudiesActivity;
import com.example.smishingdetectionapp.navigation.BottomNavCoordinator;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EducationActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);


        BottomNavCoordinator.setup(this, R.id.nav_home);

        // Back button logic
        ImageButton backButton = findViewById(R.id.education_back);
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // WebView for YouTube video
        WebView youtubeWebView = findViewById(R.id.youtubeWebView);
        WebSettings webSettings = youtubeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        youtubeWebView.setWebViewClient(new WebViewClient());
        youtubeWebView.loadUrl("https://www.youtube.com/embed/ZOZGQeG8avQ");

        // Launch quiz activity
        Button quizButton = findViewById(R.id.quiz_button);
        quizButton.setOnClickListener(v -> {
            Intent intent = new Intent(EducationActivity.this, QuizesActivity.class);
            startActivity(intent);
        });

      // Launch case studies activity
        Button caseStudiesButton = findViewById(R.id.btn_case_studies);
        caseStudiesButton.setOnClickListener(v -> {
            Intent intent = new Intent(EducationActivity.this, CaseStudiesActivity.class);
            startActivity(intent);
        });

        // Quick guide button
        Button tutorialBtn = findViewById(R.id.tutorialBtn);
        tutorialBtn.setOnClickListener(v -> {
            Intent intent = new Intent(EducationActivity.this, MainActivity.class);
            intent.putExtra("showGuide", true);
            startActivity(intent);
        });
    }
}

