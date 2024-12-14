package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.detections.YourReportsActivity;

public class ReportingActivity extends AppCompatActivity {

    private float initialY;
    private static final int SWIPE_THRESHOLD = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Menu button to open PopupMenu
        ImageButton menuButton = findViewById(R.id.report_menu);
        menuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(ReportingActivity.this, menuButton);
            popup.getMenuInflater().inflate(R.menu.report_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.saved_reports) {
                    startActivity(new Intent(ReportingActivity.this, YourReportsActivity.class));
                    return true;
                }
                return false;
            });
            popup.show();
        });

        // Initialize UI elements
        final EditText phoneNumber = findViewById(R.id.PhoneNumber);
        final EditText message = findViewById(R.id.reportmessage);
        final Button sendReportButton = findViewById(R.id.reportButton);

        // Disable the "Send Report" button initially
        sendReportButton.setEnabled(false);

        // Action for the "Send Report" button
        sendReportButton.setOnClickListener(v -> {
            try {
                // Retrieve and sanitize user input
                String rawPhoneNumber = phoneNumber.getText().toString();
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
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();

                boolean isInserted = databaseAccess.sendReport(Integer.parseInt(sanitizedPhoneNumber), sanitizedMessage);
                databaseAccess.close();

                // Handle result
                if (isInserted) {
                    phoneNumber.setText(null);
                    message.setText(null);
                    Toast.makeText(getApplicationContext(), "Report sent!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Report could not be sent!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e("ReportingActivity", "Error sending report: " + e.getMessage(), e);
                Toast.makeText(this, "An error occurred while sending the report!", Toast.LENGTH_LONG).show();
            }
        });

        // Enable/disable the "Send Report" button based on text fields
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userPhone = phoneNumber.getText().toString();
                String userMessage = message.getText().toString();
                sendReportButton.setEnabled(!userPhone.isEmpty() && !userMessage.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        phoneNumber.addTextChangedListener(afterTextChangedListener);
        message.addTextChangedListener(afterTextChangedListener);

        // Set OnTouchListener to detect swipe gestures
        findViewById(R.id.main).setOnTouchListener((v, event) -> {
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
                            startActivity(new Intent(ReportingActivity.this, SettingsActivity.class));
                            finish(); // Close the current activity
                        }
                    }
                    return true;
            }
            return false;
        });
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
        return message != null && !message.isEmpty() && message.length() <= 255;
    }

    // Helper function to sanitize input
    private String sanitizeInput(String input) {
        return input.replaceAll("[<>\"']", "").trim();
    }
}
