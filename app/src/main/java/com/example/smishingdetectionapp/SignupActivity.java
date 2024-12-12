package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.ui.login.LoginActivity;

public class SignupActivity extends AppCompatActivity {

    private static final int TERMS_REQUEST_CODE = 1001;  // Unique request code for terms acceptance
    private boolean isTermsAccepted = false;

    private EditText fullNameInput, phoneNumberInput, emailInput, passwordInput, confirmPasswordInput;
    private Button registerButton;
    private TextView termsTextView;

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

        // Disable the register button initially
        registerButton.setEnabled(false);

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

        // Add TextWatchers to input fields
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
        passwordInput.addTextChangedListener(textWatcher);
        confirmPasswordInput.addTextChangedListener(textWatcher);
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
            checkFieldsForEmptyValues(); // Re-evaluate the button state
        }
    }

    // Check if all fields are filled and terms are accepted
    private void checkFieldsForEmptyValues() {
        String fullName = fullNameInput.getText().toString();
        String phoneNumber = phoneNumberInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        registerButton.setEnabled(!fullName.isEmpty() && !phoneNumber.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && isTermsAccepted);
    }
}