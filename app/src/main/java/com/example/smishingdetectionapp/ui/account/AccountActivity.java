package com.example.smishingdetectionapp.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.example.smishingdetectionapp.UserProfileActivity;

public class AccountActivity extends SharedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Handle system window insets for immersive UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button to navigate back to the settings page
        ImageButton accountBackButton = findViewById(R.id.account_back);
        accountBackButton.setOnClickListener(v -> {
            startActivity(new Intent(AccountActivity.this, SettingsActivity.class));
            finish();
        });

        // Password change button
        Button passwordChangeButton = findViewById(R.id.passwordBtn);
        passwordChangeButton.setOnClickListener(v -> {
            PopupPW passwordBottomSheet = new PopupPW();
            passwordBottomSheet.show(getSupportFragmentManager(), "PasswordModalBottomSheet");
        });

        // Email change button
        Button emailChangeButton = findViewById(R.id.emailBtn);
        emailChangeButton.setOnClickListener(v -> {
            PopupEmail emailBottomSheet = new PopupEmail();
            emailBottomSheet.show(getSupportFragmentManager(), "EmailModalBottomSheet");
        });

        // Sign-out button
        Button signOutButton = findViewById(R.id.buttonSignOut);
        signOutButton.setOnClickListener(v -> {
            PopupSO signOutBottomSheet = new PopupSO();
            signOutBottomSheet.show(getSupportFragmentManager(), "SignOutModalBottomSheet");
        });

        // Delete account button
        Button deleteAccountButton = findViewById(R.id.account_delete);
        deleteAccountButton.setOnClickListener(v -> {
            PopupDEL deleteBottomSheet = new PopupDEL();
            deleteBottomSheet.show(getSupportFragmentManager(), "DeleteAccountModalBottomSheet");
        });

        // Phone number change button
        Button phoneChangeButton = findViewById(R.id.phoneBtn);
        phoneChangeButton.setOnClickListener(v -> {
            PopupPN phoneBottomSheet = new PopupPN();
            phoneBottomSheet.show(getSupportFragmentManager(), "PhoneModalBottomSheet");
        });

        // Profile button to open the User Profile page
        ImageButton profileButton = findViewById(R.id.profileBtn);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
    }
}
