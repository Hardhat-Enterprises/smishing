package com.example.smishingdetectionapp.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatDelegate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smishingdetectionapp.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity{

    private ViewPager2 viewPager;
    private Button skipButton;
    private Button nextButton;
    private OnBoardingSliderAdapter adapter;
    private DotsIndicator dotsIndicator;
    private TextView stepCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        skipButton = findViewById(R.id.skipButton);
        nextButton = findViewById(R.id.nextButton);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        stepCounter = findViewById(R.id.stepCounter);

        List<OnBoardingSlide> slides = new ArrayList<>();
        slides.add(new OnBoardingSlide(R.drawable.onboarding_screen_1, "Intelligent Scam Detection System", "Stay alert! Instantly identify and block suspicious messages before they reach you.."));
        slides.add(new OnBoardingSlide(R.drawable.onboarding_screen_2, "Real-Time Cyber New Alerts", "Get the latest updates on scams, breaches, and cybersecurity trends—right in your pocket."));
        slides.add(new OnBoardingSlide(R.drawable.onboarding_screen_3, "Safe browsing", "Protects you from malicious links and attachments. Adjust the sensitivity and criteria for spam detection."));

        adapter = new OnBoardingSliderAdapter(this, slides);
        viewPager.setAdapter(adapter);
        dotsIndicator.setViewPager2(viewPager);
        stepCounter.setText("1 of " + slides.size());

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
                stepCounter.setText((position + 1) + " of " + adapter.getItemCount());
                if (position == adapter.getItemCount() - 1) {
                    nextButton.setText("Let's Get Started");
                } else {
                    nextButton.setText("Next");
                }
            }
        });
    }

   private void finishIntroSlider() {
        // Start main app activity
        startActivity(new Intent(this, LoginCreateActivity.class));
        finish();
    }

}
