package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.detections.DatabaseAccess;

public class DatabaseTestActivity extends AppCompatActivity {

    private static final String TAG = "DatabaseTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        EditText emailInput = findViewById(R.id.emailInput);
        EditText verificationCodeInput = findViewById(R.id.verificationCodeInput);
        Button saveButton = findViewById(R.id.saveButton);
        Button checkButton = findViewById(R.id.checkButton);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);

        saveButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String code = verificationCodeInput.getText().toString().trim();

            if (email.isEmpty() || code.isEmpty()) {
                Toast.makeText(this, "Please enter email and code", Toast.LENGTH_SHORT).show();
                return;
            }

            databaseAccess.open();
            boolean isSaved = databaseAccess.saveVerificationCode(email, code);
            databaseAccess.close();

            if (isSaved) {
                Log.d(TAG, "Code saved successfully for email: " + email + ", Code: " + code);
                Toast.makeText(this, "Verification code saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to save code for email: " + email);
                Toast.makeText(this, "Failed to save verification code", Toast.LENGTH_SHORT).show();
            }
        });

        checkButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String code = verificationCodeInput.getText().toString().trim();

            if (email.isEmpty() || code.isEmpty()) {
                Toast.makeText(this, "Please enter email and code", Toast.LENGTH_SHORT).show();
                return;
            }

            databaseAccess.open();
            boolean isValid = databaseAccess.checkVerificationCode(email, code);
            databaseAccess.close();

            if (isValid) {
                Log.d(TAG, "Code verification successful for email: " + email + ", Code: " + code);
                Toast.makeText(this, "Verification code is valid", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Code verification failed for email: " + email + ", Entered Code: " + code);
                Toast.makeText(this, "Invalid verification code", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
