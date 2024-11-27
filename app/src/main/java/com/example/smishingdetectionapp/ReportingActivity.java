package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.detections.DatabaseAccess;

import java.util.concurrent.Executor;

public class ReportingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reporting);

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button to return to SettingsActivity
        ImageButton report_back = findViewById(R.id.report_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // Initialize UI elements
        final EditText phonenumber = findViewById(R.id.PhoneNumber);
        final EditText message = findViewById(R.id.reportmessage);
        final Button sendReportButton = findViewById(R.id.reportButton);
        final Button fingerVerifyButton = findViewById(R.id.fingerverify);

        // Disable the "Send Report" button initially
        sendReportButton.setEnabled(false);

        // Biometric authentication setup
        fingerVerifyButton.setOnClickListener(view -> {
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Please Verify")
                    .setDescription("User Authentication is required to proceed")
                    .setNegativeButtonText("Cancel")
                    .build();
            getPrompt().authenticate(promptInfo);
        });

        // Action for the "Send Report" button
        sendReportButton.setOnClickListener(v -> {
            String rawPhoneNumber = phonenumber.getText().toString();
            String rawMessage = message.getText().toString();

            // Validate inputs
            if (!isValidPhoneNumber(rawPhoneNumber)) {
                Toast.makeText(getApplicationContext(), "Invalid phone number!", Toast.LENGTH_LONG).show();
                return;
            }

            if (!isValidMessage(rawMessage)) {
                Toast.makeText(getApplicationContext(), "Invalid message content!", Toast.LENGTH_LONG).show();
                return;
            }

            String sanitizedPhoneNumber = sanitizeInput(rawPhoneNumber);
            String sanitizedMessage = sanitizeInput(rawMessage);

            // Insert into the database
            boolean isInserted = DatabaseAccess.sendReport(sanitizedPhoneNumber, sanitizedMessage);

            // Handle result
            if (isInserted) {
                phonenumber.setText(null);
                message.setText(null);
                Toast.makeText(getApplicationContext(), "Report sent!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Report could not be sent!", Toast.LENGTH_LONG).show();
            }

            // Log all reports (debugging purpose)
            try {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
                databaseAccess.open();
                databaseAccess.logAllReports();
                databaseAccess.close();
            } catch (Exception e) {
                Log.d("DatabaseAccessError", "Error occurred while accessing the database: " + e.getMessage(), e);
            }
        });

        // Enable/disable the "Send Report" button based on text fields
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userPhone = phonenumber.getText().toString();
                String userMessage = message.getText().toString();
                sendReportButton.setEnabled(!userPhone.isEmpty() && !userMessage.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        phonenumber.addTextChangedListener(afterTextChangedListener);
        message.addTextChangedListener(afterTextChangedListener);
    }

    // Biometric authentication prompt
    private BiometricPrompt getPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser(errString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                notifyUser("Authentication Succeeded!");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                notifyUser("Authentication Failed");
            }
        };

        return new BiometricPrompt(this, executor, callback);
    }

    // Show a toast message
    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Helper function to validate phone number
    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        String regex = "^\\+?[1-9]\\d{1,14}$";
        return phoneNumber.matches(regex);
    }

    // Helper function to validate message content
    private boolean isValidMessage(String message) {
        if (message == null || message.isEmpty()) {
            return false;
        }
        return message.length() <= 255;
    }

    // Helper function to sanitize input
    private String sanitizeInput(String input) {
        return input.replaceAll("[<>\"']", "").trim();
    }
}
