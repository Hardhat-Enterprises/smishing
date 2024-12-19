package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Links the activity with activity_login.xml

        // Initialize the EditText fields
        emailEditText = findViewById(R.id.emailField);
        passwordEditText = findViewById(R.id.password);

        // Initialize the Login button
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish(); // Optional: Closes the LoginActivity
            }
        });

        // Initialize the Forgot Password button
        Button forgotPasswordButton = findViewById(R.id.submitForgotPassword);
        forgotPasswordButton.setOnClickListener(v -> {
            Log.d("LoginActivity", "Forgot Password button clicked"); // Debug log
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });


        // Initialize the Register button
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, com.example.smishingdetectionapp.ui.Register.RegisterMain.class);
            startActivity(intent);
        });
    }
}
