package com.example.smishingdetectionapp.ui.Register;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
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
    private CheckBox termsCheckBox;

    // ── Inline error TextViews ──────────────────────────────────
    private TextView errorFullName;
    private TextView errorPhone;
    private TextView errorEmail;
    private TextView errorPin;
    private TextView errorPassword;
    private TextView errorConfirmPassword;
    private TextView passwordStrength;

    // ── Declared as fields so all methods can access them ──────
    private CheckBox termsCheckBox;

    // ── Declared as fields so all methods can access them ──────
    private CheckBox termsCheckBox;

    // ── Declared as fields so all methods can access them ──────
    private CheckBox termsCheckBox;

    // ── Declared as fields so all methods can access them ──────
    private CheckBox termsCheckBox;

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

        // Wire up error TextViews
        errorFullName = findViewById(R.id.error_full_name);
        errorPhone = findViewById(R.id.error_phone);
        errorEmail = findViewById(R.id.error_email);
        errorPin = findViewById(R.id.error_pin);
        errorPassword = findViewById(R.id.error_password);
        errorConfirmPassword = findViewById(R.id.error_confirm_password);
        passwordStrength = findViewById(R.id.password_strength);

        // Back button
        binding.signupBack.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Terms views
        TextView termsTextView = findViewById(R.id.terms_text);
        termsCheckBox = findViewById(R.id.terms_condition_checkbox);

        // Tapping the text opens Terms and Conditions page
        if (termsTextView != null) {
            termsTextView.setOnClickListener(v -> {
                Intent intent = new Intent(RegisterMain.this, TermsAndConditionsActivity.class);
                startActivityForResult(intent, TERMS_REQUEST_CODE);
            });
        }

        // Tapping the checkbox also opens Terms and Conditions page
        if (termsCheckBox != null) {
            termsCheckBox.setOnClickListener(v -> {
                termsCheckBox.setChecked(false);
                Intent intent = new Intent(RegisterMain.this, TermsAndConditionsActivity.class);
                startActivityForResult(intent, TERMS_REQUEST_CODE);
            });
        }

        // Register button
        Button registerButton = findViewById(R.id.registerBtn);
        registerButton.setEnabled(false);

        registerButton.setOnClickListener(v -> {
            String fullName = binding.fullNameInput.getText().toString().trim();
            String phoneNumber = binding.pnInput.getText().toString().trim();
            String email = binding.emailInput.getText().toString().trim();
            String password = binding.pwInput.getText().toString();

            if (!termsAccepted) {
                Snackbar.make(binding.getRoot(),
                        "Please accept Terms & Conditions",
                        Snackbar.LENGTH_LONG).show();
                return;
            }

            if (validateInput(fullName, phoneNumber, email, password)) {
                validateAndCheckEmail(fullName, phoneNumber, email, password);
            }
        });

        // ── Real-time validation listeners ──────────────────────

        // Full name
        binding.fullNameInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    showError(errorFullName, "Full name is required");
                } else {
                    hideError(errorFullName);
                }
            }
        });

        // Phone number — Australian format
        binding.pnInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String phone = s.toString().trim();
                if (phone.isEmpty()) {
                    hideError(errorPhone);
                    return;
                }
                String digitsOnly = phone.replaceAll("[\\s\\-().+]", "");
                boolean isValid = digitsOnly.matches("04\\d{8}") ||
                        digitsOnly.matches("0[2378]\\d{8}") ||
                        digitsOnly.matches("614\\d{8}");
                if (!isValid) {
                    showError(errorPhone, "Enter a valid Australian phone number (e.g. 04XX XXX XXX)");
                } else {
                    hideError(errorPhone);
                }
            }
        });

        // Email
        binding.emailInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String email = s.toString().trim();
                if (email.isEmpty()) {
                    hideError(errorEmail);
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    showError(errorEmail, "Enter a valid email address");
                } else {
                    hideError(errorEmail);
                }
            }
        });

        // PIN — must be 6 digits
        binding.pinInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String pin = s.toString().trim();
                if (pin.isEmpty()) {
                    hideError(errorPin);
                    return;
                }
                if (pin.length() != 6) {
                    showError(errorPin, "PIN must be exactly 6 digits");
                } else {
                    hideError(errorPin);
                }
            }
        });

        // Password strength
        binding.pwInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String pw = s.toString();
                if (pw.isEmpty()) {
                    passwordStrength.setVisibility(android.view.View.GONE);
                    hideError(errorPassword);
                    return;
                }
                updatePasswordStrength(pw);
                // Validate confirm password match if already filled
                String confirm = binding.pw2Input.getText().toString();
                if (!confirm.isEmpty()) {
                    if (!pw.equals(confirm)) {
                        showError(errorConfirmPassword, "Passwords do not match");
                    } else {
                        hideError(errorConfirmPassword);
                    }
                }
            }
        });

        // Confirm password
        binding.pw2Input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String confirm = s.toString();
                String password = binding.pwInput.getText().toString();
                if (confirm.isEmpty()) {
                    hideError(errorConfirmPassword);
                    return;
                }
                if (!confirm.equals(password)) {
                    showError(errorConfirmPassword, "Passwords do not match");
                } else {
                    hideError(errorConfirmPassword);
                }
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

    // ── Terms result ────────────────────────────────────────────
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TERMS_REQUEST_CODE) {
            Button registerButton = findViewById(R.id.registerBtn);
            if (resultCode == RESULT_OK) {
                termsAccepted = true;
                registerButton.setEnabled(true);
                if (termsCheckBox != null) termsCheckBox.setChecked(true);
            } else {
                termsAccepted = false;
                registerButton.setEnabled(false);
                if (termsCheckBox != null) termsCheckBox.setChecked(false);
            }
        }
    }

    // ── Dark / Light mode ───────────────────────────────────────
    private void setDarkMode() {
        binding.getRoot().setBackgroundColor(
                ContextCompat.getColor(this, R.color.black));
        binding.fullNameInput.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.emailInput.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.pnInput.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.pwInput.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.pw2Input.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    private void setLightMode() {
        binding.getRoot().setBackgroundColor(
                ContextCompat.getColor(this, R.color.white));
        binding.fullNameInput.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.emailInput.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.pnInput.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.pwInput.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.pw2Input.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    // ── Verification code ───────────────────────────────────────
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    // ── Input validation ────────────────────────────────────────
    private boolean validateInput(String fullName, String phoneNumber,
                                  String email, String password) {
        if (TextUtils.isEmpty(fullName)) {
            Snackbar.make(binding.getRoot(), "Enter full name",
                    Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            Snackbar.make(binding.getRoot(), "Invalid phone number",
                    Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!isValidEmailAddress(email)) {
            Snackbar.make(binding.getRoot(), "Invalid email",
                    Snackbar.LENGTH_LONG).show();
            return false;
        }
        String confirmPassword = binding.pw2Input.getText().toString();
        if (password.length() < 8 ||
                !password.matches(".*\\d.*") ||
                !password.matches(".*[A-Z].*") ||
                !password.matches(".*[a-z].*") ||
                !password.matches(".*[!@#$%^&*+=?-].*")) {
            Snackbar.make(binding.getRoot(), "Password does not meet requirements",
                    Snackbar.LENGTH_LONG).show();
            return false;
        }

    // ── Email availability check ────────────────────────────────
    private void validateAndCheckEmail(String fullName, String phoneNumber,
                                       String email, String password) {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);

        Call<SignupResponse> call = retrofitinterface.checkEmail(map);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call,
                                   Response<SignupResponse> response) {
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
                    Snackbar.make(binding.getRoot(), "Email already exists",
                            Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(binding.getRoot(), "Server error",
                            Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                Snackbar.make(binding.getRoot(), "Network error",
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // ── Email format check ──────────────────────────────────────
    private boolean isValidEmailAddress(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            String pattern =
                    "^[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
            return Pattern.matches(pattern, email);
        } catch (AddressException e) {
            return false;
        }
    }
}