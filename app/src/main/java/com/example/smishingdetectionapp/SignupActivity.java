package com.example.smishingdetectionapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.ui.login.LoginActivity;

public class SignupActivity extends AppCompatActivity {

    private static final int TERMS_REQUEST_CODE = 1001;
    private boolean isTermsAccepted = false;

    private EditText fullNameInput, phoneNumberInput, emailInput, passwordInput, confirmPasswordInput, pinInput;
    private Button registerButton;
    private TextView termsTextView;
    private View passwordStrengthBar;
    private TextView passwordStrengthLabel;

    private DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        ImageButton imageButton = findViewById(R.id.signup_back);
        termsTextView = findViewById(R.id.terms_conditions);
        registerButton = findViewById(R.id.registerBtn);
        fullNameInput = findViewById(R.id.full_name_input);
        phoneNumberInput = findViewById(R.id.pnInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.pwInput);
        confirmPasswordInput = findViewById(R.id.pw2Input);
        pinInput = findViewById(R.id.pinInput);
        passwordStrengthBar = findViewById(R.id.passwordStrengthBar);
        passwordStrengthLabel = findViewById(R.id.passwordStrengthLabel);

        // Disable the register button initially
        registerButton.setEnabled(false);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        // Back button functionality
        imageButton.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Open Terms and Conditions Activity on click
        termsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, TermsAndConditionsActivity.class);
            startActivityForResult(intent, TERMS_REQUEST_CODE);
        });

        // TextWatcher for all fields
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFieldsForEmptyValues();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        fullNameInput.addTextChangedListener(textWatcher);
        phoneNumberInput.addTextChangedListener(textWatcher);
        emailInput.addTextChangedListener(textWatcher);
        confirmPasswordInput.addTextChangedListener(textWatcher);
        pinInput.addTextChangedListener(textWatcher);

        // Separate watcher for password to update strength indicator
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordStrength(s.toString());
                checkFieldsForEmptyValues();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Log.d("SignupActivity", "Rsaegister button clicked!");
        registerButton.setOnClickListener(v -> {
            Log.d("SignupActivity", "Register button clicked!");
            String name = fullNameInput.getText().toString();
            String phoneNumber = phoneNumberInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();
            String pin = pinInput.getText().toString();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phoneNumber.length() != 10 || !phoneNumber.matches("\\d+")) {
                Toast.makeText(this, "Invalid phone number. Must be 10 digits.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pin.length() != 6 || !pin.matches("\\d+")) {
                Toast.makeText(this, "Invalid PIN. Must be 6 digits.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                boolean isInserted = databaseAccess.insertLogin(name, email, phoneNumber, password, pin);
                if (isInserted) {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Error saving user details. Try again.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Update the password strength bar and label
    private void updatePasswordStrength(String password) {
        if (password.isEmpty()) {
            passwordStrengthBar.setVisibility(View.GONE);
            passwordStrengthLabel.setVisibility(View.GONE);
            return;
        }

        passwordStrengthBar.setVisibility(View.VISIBLE);
        passwordStrengthLabel.setVisibility(View.VISIBLE);

        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[0-9].*")) strength++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;':\",./<>?].*")) strength++;

        if (strength <= 1) {
            passwordStrengthBar.setBackgroundColor(Color.RED);
            passwordStrengthLabel.setText("Weak");
            passwordStrengthLabel.setTextColor(Color.RED);
        } else if (strength == 2 || strength == 3) {
            passwordStrengthBar.setBackgroundColor(Color.parseColor("#FFA500")); // Orange
            passwordStrengthLabel.setText("Medium");
            passwordStrengthLabel.setTextColor(Color.parseColor("#FFA500"));
        } else {
            passwordStrengthBar.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
            passwordStrengthLabel.setText("Strong");
            passwordStrengthLabel.setTextColor(Color.parseColor("#4CAF50"));
        }
    }

    // Handle the result from Terms and Conditions activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TERMS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                isTermsAccepted = true;
                termsTextView.setTextColor(getResources().getColor(R.color.blue_grotto));
            }
            checkFieldsForEmptyValues();
        }
    }

    // Check if all fields are filled and terms are accepted
    private void checkFieldsForEmptyValues() {
        String fullName = fullNameInput.getText().toString();
        String phoneNumber = phoneNumberInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        String pin = pinInput.getText().toString();

        boolean allFieldsFilled = !fullName.isEmpty() && !phoneNumber.isEmpty() &&
                !email.isEmpty() && !password.isEmpty() &&
                !confirmPassword.isEmpty() && !pin.isEmpty() &&
                isTermsAccepted;
        Log.d("SignupActivity", "All fields valid: " + allFieldsFilled);
        registerButton.setEnabled(allFieldsFilled);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseAccess.close();
    }
}
