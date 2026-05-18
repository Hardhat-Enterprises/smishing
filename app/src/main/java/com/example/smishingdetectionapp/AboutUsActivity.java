package com.example.smishingdetectionapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.navigation.BottomNavCoordinator;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutUsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        BottomNavCoordinator.setup(this, R.id.nav_settings);

        // Back button
        ImageButton backButton = findViewById(R.id.about_back);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(AboutUsActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });

        // Set up clickable email link with custom style
        TextView contactUsText = findViewById(R.id.contactUsText);
        String fullText = "For inquiries, please email us at: support@hardhat.com";
        String email = "support@hardhat.com";

        SpannableString spannableString = getSpannableString(fullText, email);

        contactUsText.setText(spannableString);
        contactUsText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @NonNull
    private static SpannableString getSpannableString(String fullText, String email) {
        int startIndex = fullText.indexOf(email);
        int endIndex = startIndex + email.length();

        SpannableString spannableString = new SpannableString(fullText);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(android.net.Uri.parse("mailto:" + email));
                widget.getContext().startActivity(Intent.createChooser(emailIntent, "Send Email"));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#00008B")); // Dark Blue color
                ds.setFakeBoldText(true);                  // Bold text
                ds.setUnderlineText(false);                // Remove underline if you prefer
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
