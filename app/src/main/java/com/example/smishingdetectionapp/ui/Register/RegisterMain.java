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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SignupActivity;
import com.example.smishingdetectionapp.TermsAndConditionsActivity;
import com.example.smishingdetectionapp.databinding.ActivitySignupBinding;
import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterMain extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivitySignupBinding binding;

    private Retrofit retrofit;
    private Retrofitinterface retrofitinterface;
    private String BASE_URL = "http://192.168.88.154:3000"; // Your server URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Initialize Retrofit
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

        // Navigate to Terms and Conditions Activity
        TextView termsTextView = findViewById(R.id.terms_conditions);
        termsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterMain.this, TermsAndConditionsActivity.class);
            startActivity(intent);
        });

        // Enable register button only when terms are accepted
        CheckBox termsCheckbox = findViewById(R.id.terms_conditions_checkbox);
        Button registerButton = findViewById(R.id.registerBtn);
        registerButton.setEnabled(false); // Disable the register button initially

        termsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            registerButton.setEnabled(isChecked);
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the inputs
                String fullName = binding.fullNameInput.getText().toString();
                String phoneNumber = binding.pnInput.getText().toString();
                String email = binding.emailInput.getText().toString();
                String password = binding.pwInput.getText().toString(); // Hash this password

                // Assume that registration process is done here
                if (validateInput(fullName, phoneNumber, email, password)) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("FullName", fullName);
                    map.put("PhoneNumber", phoneNumber);
                    map.put("Email", email);
                    map.put("Password", password);

                    Call<Void> call = retrofitinterface.executeSignup(map);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Mail(email);
                                Intent intent = new Intent(RegisterMain.this, MainActivity.class);
                                startActivity(intent);
                                
                                } else if (response.code() == 409) {
                                Snackbar.make(binding.getRoot(), "Email already exists.", Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(binding.getRoot(), "Signup failed. Try again.", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Snackbar.make(binding.getRoot(), t.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void Mail(String email) {
        String subject = "Hello";
        String message = "This is the message";

        JavaMailAPI javaMailAPI = new JavaMailAPI(this, email, subject, message);
        javaMailAPI.execute();
    }

    private boolean validateInput(String fullName, String phoneNumber, String email, String password) {
        // Validate full name
        if (TextUtils.isEmpty(fullName)) {
            Snackbar.make(binding.getRoot(), "Please enter your full name.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        // Validate phone number
        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            Snackbar.make(binding.getRoot(), "Please enter a valid phone number.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        // Validate email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(binding.getRoot(), "Please enter a valid email address.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        // Validate passwords
        String confirmPassword = binding.pw2Input.getText().toString();
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Snackbar.make(binding.getRoot(), "Please fill out the password fields.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Snackbar.make(binding.getRoot(), "Passwords do not match.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_signup);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }




}
