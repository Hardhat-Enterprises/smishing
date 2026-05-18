package com.example.smishingdetectionapp.ui.Register;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.smishingdetectionapp.BuildConfig;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.DataBase.SignupResponse;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.TermsAndConditionsActivity;
import com.example.smishingdetectionapp.databinding.ActivitySignupBinding;
import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Random;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterMain extends AppCompatActivity {

    private static final int TERMS_REQUEST_CODE = 1001;
    private ActivitySignupBinding binding;
    private Retrofit retrofit;
    private Retrofitinterface retrofitinterface;
    private String BASE_URL = BuildConfig.SERVERIP;

    private boolean termsAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrofit setup
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitinterface = retrofit.create(Retrofitinterface.class);

        // Back button
        binding.signupBack.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Link Terms and Conditions
        TextView termsTextView = findViewById(R.id.terms_text);
        termsCheckBox = findViewById(R.id.terms_condition_checkbox);

        // Tapping the text opens Terms and Conditions page
        termsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterMain.this, TermsAndConditionsActivity.class);
            startActivityForResult(intent, TERMS_REQUEST_CODE);
        });

        // Tapping the checkbox also opens Terms and Conditions page
        termsCheckBox.setOnClickListener(v -> {
            termsCheckBox.setChecked(false);
            Intent intent = new Intent(RegisterMain.this, TermsAndConditionsActivity.class);
            startActivityForResult(intent, TERMS_REQUEST_CODE);
        });

        // Set up register button
        Button registerButton = findViewById(R.id.registerBtn);
        registerButton.setEnabled(false);

        registerButton.setOnClickListener(v -> {
            String fullName = binding.fullNameInput.getText().toString();
            String phoneNumber = binding.pnInput.getText().toString();
            String email = binding.emailInput.getText().toString();
            String password = binding.pwInput.getText().toString();

            if (!termsAccepted) {
                Snackbar.make(binding.getRoot(), "Please accept Terms & Conditions", Snackbar.LENGTH_LONG).show();
                return;
            }

            if (validateInput(fullName, phoneNumber, email, password)) {
                validateAndCheckEmail(fullName, phoneNumber, email, password);
            }
        });

        // Dark mode support
        int nightModeFlags = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            setDarkMode();
        } else {
            setLightMode();
        }
    }

    // ---------------- TERMS RESULT ----------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TERMS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Button registerButton = findViewById(R.id.registerBtn);
                registerButton.setEnabled(true);
                termsCheckBox.setChecked(true);
            } else {
                termsCheckBox.setChecked(false);
                findViewById(R.id.registerBtn).setEnabled(false);
            }
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void setLightMode() {
        binding.getRoot().setBackgroundColor(ContextCompat.getColor(this, R.color.white));
    }

    // ---------------- VALIDATION ----------------
    private boolean validateInput(String fullName, String phoneNumber, String email, String password) {

        if (TextUtils.isEmpty(fullName)) {
            Snackbar.make(binding.getRoot(), "Enter full name", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            Snackbar.make(binding.getRoot(), "Invalid phone number", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!isValidEmailAddress(email)) {
            Snackbar.make(binding.getRoot(), "Invalid email", Snackbar.LENGTH_LONG).show();
            return false;
        }

        String confirmPassword = binding.pw2Input.getText().toString();

        if (password.length() < 8 ||
                !password.equals(confirmPassword) ||
                !password.matches(".*\\d.*") ||
                !password.matches(".*[A-Z].*") ||
                !password.matches(".*[a-z].*") ||
                !password.matches(".*[!@#$%^&*+=?-].*")) {

            Snackbar.make(binding.getRoot(), "Password does not meet requirements", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    // ---------------- EMAIL CHECK ----------------
    private void validateAndCheckEmail(String fullName, String phoneNumber, String email, String password) {

        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);

        Call<SignupResponse> call = retrofitinterface.checkEmail(map);

        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {

                if (response.isSuccessful()) {

                    String code = generateVerificationCode();

                    Intent intent = new Intent(RegisterMain.this, EmailVerify.class);
                    intent.putExtra("fullName", fullName);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("code", code);

                    startActivity(intent);

                } else if (response.code() == 409) {
                    Snackbar.make(binding.getRoot(), "Email already exists", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(binding.getRoot(), "Server error", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                Snackbar.make(binding.getRoot(), "Network error", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private boolean isValidEmailAddress(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();

            String pattern = "^[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
            return Pattern.matches(pattern, email);

        } catch (AddressException e) {
            return false;
        }
    }
}