package com.example.smishingdetectionapp.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.example.smishingdetectionapp.SharedActivity;
import com.example.smishingdetectionapp.news.NewsActivity;
import com.example.smishingdetectionapp.ui.account.PopupPW;
import com.example.smishingdetectionapp.ui.account.PopupEmail;
import com.example.smishingdetectionapp.ui.account.PopupSO;
import com.example.smishingdetectionapp.ui.account.confirmDeletePopup;
import com.example.smishingdetectionapp.ui.account.PopupPN;
import com.example.smishingdetectionapp.ui.account.PopupST;

import java.util.concurrent.Executor;

public class AccountActivity extends SharedActivity {

    private BiometricPrompt biometricPrompt;
    private boolean isAuthenticated = false;
    private static final int TIMEOUT_MILLIS = 30000; // 30 seconds timeout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        // Handle insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button to go back to settings page
        ImageButton account_back = findViewById(R.id.account_back);
        account_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // Opens the password change window
        Button password_changeBtn = findViewById(R.id.passwordBtn);
        password_changeBtn.setOnClickListener(v -> {
            PopupPW bottomSheet = new PopupPW();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        // Opens the email change window
        Button email_changeBtn = findViewById(R.id.emailBtn);
        email_changeBtn.setOnClickListener(v -> {
            PopupEmail bottomSheet = new PopupEmail();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        // Opens the sign out window
        Button sign_outBtn = findViewById(R.id.buttonSignOut);
        sign_outBtn.setOnClickListener(v -> {
            PopupSO bottomSheet = new PopupSO();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        // Opens the delete account confirmation popup
        Button delete_accBtn = findViewById(R.id.account_delete);
        delete_accBtn.setOnClickListener(v -> {
            confirmDeletePopup confirmDeletePopup = new confirmDeletePopup(AccountActivity.this);
            confirmDeletePopup.show();
        });

        // Opens the phone number change window
        Button change_phone_numberBtn = findViewById(R.id.phoneBtn);
        change_phone_numberBtn.setOnClickListener(v -> {
            PopupPN bottomSheet = new PopupPN();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        // Button to open session timeout settings
        Button buttonSessionTimeout = findViewById(R.id.buttonSessionTimeout);
        buttonSessionTimeout.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, PopupST.class);
            startActivity(intent);
        });

        // Account button with biometric authentication
        Button accountBtn = findViewById(R.id.accountBtn);
        accountBtn.setOnClickListener(v -> triggerBiometricAuthenticationWithTimeout());

        // Bottom Navigation setup
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_settings);

        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                return true;
            }
            return false;
        });
    }

    // Trigger biometric authentication with timeout
    private void triggerBiometricAuthenticationWithTimeout() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentication Required")
                .setDescription("Please authenticate to access your account settings")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();

        // Start the authentication process
        biometricPrompt = getPrompt();
        biometricPrompt.authenticate(promptInfo);

        // Start the timeout timer
        startTimeoutTimer();
    }

    // BiometricPrompt setup
    private BiometricPrompt getPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser("Authentication Error: " + errString);
                redirectToSettingsActivity();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                notifyUser("Authentication Succeeded!");
                isAuthenticated = true; // Mark as authenticated
                openAccountActivity(); // Proceed to AccountActivity
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                notifyUser("Authentication Failed");
            }
        };

        return new BiometricPrompt(this, executor, callback);
    }

    // Start a timeout timer for authentication
    private void startTimeoutTimer() {
        new Handler().postDelayed(() -> {
            if (!isAuthenticated) { // If authentication hasn't occurred within the timeout
                notifyUser("Authentication timed out. Redirecting to Settings...");
                biometricPrompt.cancelAuthentication(); // Cancel the ongoing authentication
                redirectToSettingsActivity(); // Redirect to SettingsActivity on timeout
            }
        }, TIMEOUT_MILLIS);
    }

    // Redirect to SettingsActivity
    private void redirectToSettingsActivity() {
        Intent intent = new Intent(AccountActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish(); // Ensure the current activity is closed
    }

    // Open AccountActivity
    private void openAccountActivity() {
        Intent intent = new Intent(AccountActivity.this, AccountActivity.class);
        startActivity(intent);
        finish(); // Close SettingsActivity if AccountActivity is opened
    }

    // Show a toast message
    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
