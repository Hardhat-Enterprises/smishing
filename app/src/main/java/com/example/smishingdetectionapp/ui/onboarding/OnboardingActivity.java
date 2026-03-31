package com.example.smishingdetectionapp.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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
    private DotsIndicator dotsIndicator; // Added this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        skipButton = findViewById(R.id.skipButton);
        nextButton = findViewById(R.id.nextButton);
        dotsIndicator = findViewById(R.id.dotsIndicator); // Initialize DotsIndicator

        List<OnBoardingSlide> slides = new ArrayList<>();
        slides.add(new OnBoardingSlide(R.drawable.onboarding_screen_1, getString(R.string.onboarding_title_1), getString(R.string.onboarding_desc_1)));
        slides.add(new OnBoardingSlide(R.drawable.onboarding_screen_2, getString(R.string.onboarding_title_2), getString(R.string.onboarding_desc_2)));
        slides.add(new OnBoardingSlide(R.drawable.onboarding_screen_3, getString(R.string.onboarding_title_3), getString(R.string.onboarding_desc_3)));

        adapter = new OnBoardingSliderAdapter(this, slides);
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
                    nextButton.setText(getString(R.string.onboarding_btn_get_started));
                } else {
                    nextButton.setText(getString(R.string.onboarding_btn_next));
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
