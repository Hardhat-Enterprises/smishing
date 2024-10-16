package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smishingdetectionapp.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class IntroActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button skipButton;
    private Button nextButton;
    private IntroSliderAdapter adapter;
    private DotsIndicator dotsIndicator; // Add this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        viewPager = findViewById(R.id.viewPager);
        skipButton = findViewById(R.id.skipButton);
        nextButton = findViewById(R.id.nextButton);
        dotsIndicator = findViewById(R.id.dotsIndicator); // Initialize DotsIndicator

        List<IntroSlide> slides = new ArrayList<>();
        slides.add(new IntroSlide(R.drawable.img_1, "Solution for detecting and eliminating spam!", "Protect your inbox from unwanted and harmful messages with our advanced spam detection technology."));
        slides.add(new IntroSlide(R.drawable.img_1, "You'll receive only the messages that matter to you", "It identifies and filters out spam, phishing attempts, and unwanted advertisements."));
        slides.add(new IntroSlide(R.drawable.img_1, "Safe browsing", "Protects you from malicious links and attachments. Adjust the sensitivity and criteria for spam detection."));

        adapter = new IntroSliderAdapter(this, slides);
        viewPager.setAdapter(adapter);
        dotsIndicator.setViewPager2(viewPager); // Connect ViewPager2 with DotsIndicator

        skipButton.setOnClickListener(v -> finishIntroSlider());

        nextButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                finishIntroSlider();
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == adapter.getItemCount() - 1) {
                    nextButton.setText("Let's Start");
                } else {
                    nextButton.setText("Next");
                }
            }
        });
    }

    private void finishIntroSlider() {
        // Start your main app activity
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}