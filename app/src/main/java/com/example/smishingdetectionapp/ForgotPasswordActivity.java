package com.example.smishingdetectionapp;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


public class ForgotPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        EditText emailField = findViewById(R.id.emailField);
        Button submitButton = findViewById(R.id.submitForgotPassword);

        submitButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                // Perform backend password reset operation
                Toast.makeText(this, "Password reset link sent to " + email, Toast.LENGTH_LONG).show();
                // Go back to LoginActivity or clear the screen
                finish();
            }
        });
    }

}
