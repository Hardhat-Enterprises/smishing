package com.example.smishingdetectionapp.ui.login;

import android.app.Activity;
import android.content.Intent;

import com.example.smishingdetectionapp.BuildConfig;
import com.example.smishingdetectionapp.DataBase.DBresult;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.MainActivity;

import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.smishingdetectionapp.BuildConfig;
import com.example.smishingdetectionapp.DataBase.DBresult;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.DataBase.UserResponse;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.databinding.ActivityLoginBinding;


import com.example.smishingdetectionapp.ui.Register.RegisterMain;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private Retrofit retrofit;
    private Retrofitinterface retrofitinterface;
    private String BASE_URL = BuildConfig.SERVERIP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        // Initialize Retrofit for network communication
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitinterface = retrofit.create(Retrofitinterface.class);

        // Check if the user is already logged in


        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.loginButton;
        final ProgressBar loadingProgressBar = binding.progressbar;





        loginButton.setOnClickListener(v -> handleLogin());

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterMain.class));
            finish();
        });



    }
    private void handleLogin() {
        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Validate email and password input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare the login credentials for the backend
        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("password", password);

        // Make the login request to the backend
        Call<DBresult> call = retrofitinterface.executeLogin(map);
        call.enqueue(new Callback<DBresult>() {
            @Override
            public void onResponse(Call<DBresult> call, Response<DBresult> response) {
                if (response.isSuccessful()) {
                    DBresult result = response.body();
                    if (result != null && result.getToken() != null) {
                        // JWT token received, store it in SharedPreferences
                        storeToken(result.getToken());
                        // After storing the token, fetch user details
                        fetchUserDetails(result.getToken());
                        navigateToMainActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed. No token received.", Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed. Try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DBresult> call, Throwable throwable) {
                Toast.makeText(LoginActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Store JWT token in SharedPreferences
    private void storeToken(String token) {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("jwt_token", token);
        editor.apply();

        Log.d("LoginActivity", "JWT Token stored: " + token);
    }


    // Fetch user details using the stored JWT token
    private void fetchUserDetails(String token) {
        // Use the JWT token from SharedPreferences or from login response
        Call<UserResponse> call = retrofitinterface.getUserDetails("Bearer " + token);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    UserResponse userDetails = response.body();
                    // Update the UI with user details or navigate to another activity
                    Toast.makeText(LoginActivity.this, "Welcome, " + userDetails.getName(), Toast.LENGTH_LONG).show();
                } else if (response.code() == 401) {
                    Toast.makeText(LoginActivity.this, "Unauthorized: Invalid or expired token", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to fetch user details", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    // Navigate to the main activity after successful login
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();  // Ensure LoginActivity is finished and removed from the back stack


    }







}
