package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VerificationCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        EditText codeField = findViewById(R.id.verificationCodeField);
        Button verifyButton = findViewById(R.id.verifyCodeButton);

        String email = getIntent().getStringExtra("email");
        String verificationCode = getIntent().getStringExtra("code"); // Get passed code

        verifyButton.setOnClickListener(v -> {
            String enteredCode = codeField.getText().toString().trim();

            if (enteredCode.isEmpty()) {
                Toast.makeText(this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
                return;
            }

            if (enteredCode.equals(verificationCode)) {
                // Success
                Intent intent = new Intent(this, ResetPasswordActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid verification code", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
