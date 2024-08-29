package com.example.smishingdetectionapp.ui.Register;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.BuildConfig;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.DataBase.SignupResponse;
import com.example.smishingdetectionapp.MainActivity;
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

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterMain extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private Retrofit retrofit;
    private Retrofitinterface retrofitinterface;
    private String BASE_URL = BuildConfig.SERVERIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitinterface = retrofit.create(Retrofitinterface.class);

        ImageButton imageButton = findViewById(R.id.signup_back);
        imageButton.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        TextView termsTextView = findViewById(R.id.terms_conditions);
        termsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterMain.this, TermsAndConditionsActivity.class);
            startActivity(intent);
        });

        CheckBox termsCheckbox = findViewById(R.id.terms_conditions_checkbox);
        Button registerButton = findViewById(R.id.registerBtn);
        registerButton.setEnabled(false);

        termsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            registerButton.setEnabled(isChecked);
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = binding.fullNameInput.getText().toString();
                String phoneNumber = binding.pnInput.getText().toString();
                String email = binding.emailInput.getText().toString();
                String password = binding.pwInput.getText().toString();

                HashMap<String, String> map = new HashMap<>();

                if (validateInput(fullName, phoneNumber, email, password)) {
                    validateAndCheckEmail(fullName, phoneNumber, email, password);
                }
            }
        });
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generates a random 6-digit code
        return String.valueOf(code);
    }

    private void sendVerificationEmail(String email, String verificationCode) {
        String subject = "Your Verification Code";
        String message = "Your verification code is: " + verificationCode;

        JavaMailAPI javaMailAPI = new JavaMailAPI(this, email, subject, message);
        javaMailAPI.execute();
    }

    private boolean validateInput(String fullName, String phoneNumber, String email, String password) {
        if (TextUtils.isEmpty(fullName)) {
            Snackbar.make(binding.getRoot(), "Please enter your full name.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            Snackbar.make(binding.getRoot(), "Please enter a valid phone number.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!isValidEmailAddress(email)) {
            Snackbar.make(binding.getRoot(), "Please enter a valid email address.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        String confirmPassword = binding.pw2Input.getText().toString();
        if (password.length() < 8) {
            Snackbar.make(binding.getRoot(), "Password must be at least 8 characters long.", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Snackbar.make(binding.getRoot(), "Passwords do not match.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            Snackbar.make(binding.getRoot(), "Please add number to password", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            Snackbar.make(binding.getRoot(), "Please add Upper case to password", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            Snackbar.make(binding.getRoot(), "Please add lower case to password", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!password.matches(".*[!@#$%^&*+=?-].*")) {
            Snackbar.make(binding.getRoot(), "Please add special character to password ", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
    private void validateAndCheckEmail(final String fullName, final String phoneNumber, final String email, final String password) {
        if (isValidEmailAddress(email)) {
            HashMap<String, String> map = new HashMap<>();
            // Step 2: Check if the email exists in the database via an API call
            Call<SignupResponse> call = retrofitinterface.executeSignup(map);
            call.enqueue(new Callback<SignupResponse>() {
                @Override
                public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                    if (response.code() != 409) {
                        // Email does not exist, proceed with verification
                        String verificationCode = generateVerificationCode(); // Generate the 6-digit code
                        sendVerificationEmail(email, verificationCode); // Send email

                        map.put("Code", verificationCode);
                        // Navigate to the EmailVerify page with the email and verification code
                        Intent intent = new Intent(RegisterMain.this, EmailVerify.class);
                        intent.putExtra("fullName", fullName);
                        intent.putExtra("phoneNumber", phoneNumber);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        intent.putExtra("code", verificationCode);
                        startActivity(intent);


                    } else if (response.code() == 409) {
                        // Email already exists
                        Snackbar.make(binding.getRoot(), "Email already exists.", Snackbar.LENGTH_LONG).show();

                    } else {
                        // Some other error
                        Snackbar.make(binding.getRoot(), "Error checking email. Please try again.", Snackbar.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(Call<SignupResponse> call, Throwable t) {
                    Snackbar.make(binding.getRoot(), "Network error. Please try again.", Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            Snackbar.make(binding.getRoot(), "Invalid email address.", Snackbar.LENGTH_LONG).show();
        }
    }
    private boolean isValidEmailAddress(String email) {


        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();

            String emailPattern = "^[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*"
                    + "@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

            if (!Pattern.matches(emailPattern, email) || email.contains("..") || email.startsWith(".") || email.endsWith(".")) {
                result = false;
            }
        } catch (AddressException ex) {
            result = false;
        }

        return result;
    }
}
