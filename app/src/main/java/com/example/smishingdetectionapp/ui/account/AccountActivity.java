package com.example.smishingdetectionapp.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;

public class AccountActivity extends AppCompatActivity {

    private float initialY; // Variable to track the initial Y position for swipe detection
    private static final int SWIPE_THRESHOLD = 50; // Threshold for swipe detection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account); // Ensure this matches your XML layout file name

        // Set up window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button to go back to settings page
        ImageButton accountBackButton = findViewById(R.id.account_back);
        accountBackButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // Opens the password change window
        Button passwordChangeButton = findViewById(R.id.passwordBtn);
        passwordChangeButton.setOnClickListener(v -> {
            PopupPW bottomSheet = new PopupPW();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        // Opens the email change window
        Button emailChangeButton = findViewById(R.id.emailBtn);
        emailChangeButton.setOnClickListener(v -> {
            PopupEmail bottomSheet = new PopupEmail();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        // Opens the phone number change window
        Button changePhoneNumberButton = findViewById(R.id.phoneBtn);
        changePhoneNumberButton.setOnClickListener(v -> {
            PopupPN bottomSheet = new PopupPN();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        // Opens the sign out confirmation window
        Button signOutButton = findViewById(R.id.buttonSignOut);
        signOutButton.setOnClickListener(v -> {
            PopupSO bottomSheet = new PopupSO();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        // Opens the delete account confirmation window
        Button deleteAccountButton = findViewById(R.id.account_delete);
        deleteAccountButton.setOnClickListener(v -> {
            PopupDEL bottomSheet = new PopupDEL();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        // Set OnTouchListener to detect swipe gestures on the ScrollView
        ScrollView scrollView = findViewById(R.id.scroll_view); // Ensure you have a ScrollView in your layout
        scrollView.setOnTouchListener(this::onTouch);
    }

    // OnTouch method to handle swipe gestures
    private boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialY = event.getY();
                return true;

            case MotionEvent.ACTION_UP:
                float finalY = event.getY();
                float deltaY = finalY - initialY;

                if (Math.abs(deltaY) > SWIPE_THRESHOLD) {
                    if (deltaY > 0) {
                        // Swipe down detected
                        startActivity(new Intent(AccountActivity.this, SettingsActivity.class));
                        finish(); // Close the current activity
                    }
                }
                return true;
        }
        return false;
    }
}