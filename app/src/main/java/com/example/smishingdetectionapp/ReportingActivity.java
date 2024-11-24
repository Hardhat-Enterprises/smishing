package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.detections.DatabaseAccess;

public class ReportingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reporting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button to go back to settings dashboard
        ImageButton report_back = findViewById(R.id.report_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        final EditText phonenumber = findViewById(R.id.PhoneNumber);
        final EditText message = findViewById(R.id.reportmessage);
        final Button sendReportButton = findViewById(R.id.reportButton);

        // DATABASE REPORT FUNCTION
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

            String sanitizedPhoneNumber = sanitizeInput(rawPhoneNumber); // probably not necessary because only numbers are allowed
            String sanitizedRawMessage = sanitizeInput(rawMessage);

            // Insert into the database
            boolean isInserted = DatabaseAccess.sendReport(sanitizedPhoneNumber, sanitizedRawMessage);

            // Handle result
            if (isInserted) {
                phonenumber.setText(null);
                message.setText(null);
                Toast.makeText(getApplicationContext(), "Report sent!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Report could not be sent!", Toast.LENGTH_LONG).show();
            }
        });

        // For enabling the report button when both text fields are filled in.
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userPhone = phonenumber.getText().toString();
                String userMessage = message.getText().toString();
                sendReportButton.setEnabled(!userPhone.isEmpty() && !userMessage.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        phonenumber.addTextChangedListener(afterTextChangedListener);
        message.addTextChangedListener(afterTextChangedListener);
    }

    // Helper function to validate phone number
    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false; // Phone number cannot be null or empty
        }

        // Regex to validate international and local phone numbers
        // ^\+?[1-9]\d{1,14}$:
        // - ^\+?      : Optional "+" at the start
        // - [1-9]     : Country codes do not start with 0
        // - \d{1,14}  : National and international numbers up to 15 digits
        String regex = "^\\+?[1-9]\\d{1,14}$";

        return phoneNumber.matches(regex);
    }


    // Helper function to validate message content
    private boolean isValidMessage(String message) {
        if (message == null || message.isEmpty()) {
            return false; // Message cannot be null or empty
        }
        if (message.length() > 255) { // Limit message length
            return false;
        }
        return true;
    }

    // Helper function to sanitize input
    private String sanitizeInput(String input) {
        return input.replaceAll("[<>\"']", "").trim(); // Removes potentially harmful characters
    }
}
