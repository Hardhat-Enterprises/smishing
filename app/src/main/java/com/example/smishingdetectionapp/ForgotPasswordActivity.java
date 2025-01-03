package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.detections.DatabaseAccess;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Log.d("ForgotPasswordActivity", "ForgotPasswordActivity started");

        EditText emailField = findViewById(R.id.emailField);
        Button submitButton = findViewById(R.id.submitForgotPassword);

        submitButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                try {
                    String verificationCode = String.format("%06d", new java.util.Random().nextInt(999999));
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
                    databaseAccess.open();
                    boolean isSaved = databaseAccess.saveVerificationCode(email, verificationCode);
                    databaseAccess.close();

                    if (!isSaved) {
                        runOnUiThread(() -> Toast.makeText(this, "Failed to save verification code", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    boolean isSent = MailSender.sendVerificationCode(email, verificationCode, this);

                    runOnUiThread(() -> {
                        if (isSent) {
                            Toast.makeText(this, "Verification code sent successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, VerificationCodeActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("code", verificationCode); // Pass the code
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e("ForgotPasswordActivity", "Error: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }
}
