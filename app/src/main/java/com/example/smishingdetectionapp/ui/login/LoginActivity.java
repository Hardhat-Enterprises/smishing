package com.example.smishingdetectionapp.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.smishingdetectionapp.BuildConfig;
import com.example.smishingdetectionapp.DataBase.DBresult;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.databinding.ActivityLoginBinding;
import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.ui.Register.RegisterMain;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private Retrofit retrofit;
    private Retrofitinterface retrofitinterface;
    private DatabaseAccess databaseAccess;
    private String BASE_URL = BuildConfig.SERVERIP;
    private boolean isPasswordVisible = false;
    private boolean isPinLogin = false;

    // Kept as fields so handleLoginDialog can access them
    private EditText usernameEditText;
    private EditText passwordEditText;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                isDarkMode
                        ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
                        : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);

        // Block screenshots and screen recording
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitinterface = retrofit.create(Retrofitinterface.class);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        // Assign to fields so other methods can access them
        usernameEditText = binding.email;
        passwordEditText = binding.password;

        final Button loginButton = binding.loginButton;
        final ProgressBar loadingProgressBar = binding.progressbar;
        final SignInButton googleBtn = binding.googleBtn;
        final Button registerButton = binding.registerButton;
        final ImageButton togglePasswordVisibility = binding.togglePasswordVisibility;
        final Button togglePinLogin = binding.togglePinLogin;
        final TextView forgotPasswordButton = binding.forgotPasswordButton;

        // Toggle PIN / Password login
        togglePinLogin.setOnClickListener(v -> {
            passwordEditText.setText("");
            if (isPinLogin) {
                passwordEditText.setHint("Password");
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                loginButton.setText("Login");
                togglePinLogin.setText("Login with PIN");
                isPinLogin = false;
            } else {
                passwordEditText.setHint("Enter 6-digit PIN");
                passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                loginButton.setText("Sign in with PIN");
                togglePinLogin.setText("Sign in with Password");
                isPinLogin = true;
            }
            passwordEditText.requestFocus();
        });

        // Login button
        loginButton.setOnClickListener(v -> {
            String input = passwordEditText.getText().toString();
            if (isPinLogin) {
                if (input.length() != 6) {
                    passwordEditText.setError("PIN must contain 6 digits");
                    return;
                }
                loginWithPin(input);
            } else {
                String email = usernameEditText.getText().toString();
                if (email.isEmpty() || input.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginWithPassword(email, input);
            }
        });

        // Forgot password
        forgotPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Register
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterMain.class));
            finish();
        });

        // Google Sign-In setup
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        gsc.signOut().addOnCompleteListener(task -> {
            // Silent sign-out on launch to allow fresh login
        });

        googleBtn.setOnClickListener(v -> {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                signOutGoogle(() -> signInGoogle());
            } else {
                signInGoogle();
            }
        });

        // Password visibility toggle
        togglePasswordVisibility.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.visibilityoff);
                isPasswordVisible = false;
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                togglePasswordVisibility.setImageResource(R.drawable.visibility);
                isPasswordVisible = true;
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });
    }

    // ── Google Sign-In ──────────────────────────────────────────
    void signInGoogle() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    void signOutGoogle(Runnable onSignOutComplete) {
        gsc.signOut().addOnCompleteListener(task -> {
            Toast.makeText(this, "Successfully signed out of Google account.", Toast.LENGTH_SHORT).show();
            onSignOutComplete.run();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToMainActivity();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Authentication was unsuccessful. Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ── Login logic ─────────────────────────────────────────────
    private void loginWithPin(String pin) {
        if (databaseAccess.validatePin(pin)) {
            navigateToMainActivity();
        } else {
            Toast.makeText(LoginActivity.this, "Invalid PIN entered", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginWithPassword(String email, String password) {
        if (canUseDebugBypassWithPassword(email, password)) {
            Toast.makeText(this, "Debug login successful", Toast.LENGTH_SHORT).show();
            navigateToMainActivity();
            return;
        }
        if (databaseAccess.validateLogin(email, password)) {
            navigateToMainActivity();
            return;
        }
        handleLoginDialog(email, password);
    }

    private void handleLoginDialog(String email, String password) {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("password", password);

        Call<DBresult> call = retrofitinterface.executeLogin(map);
        call.enqueue(new Callback<DBresult>() {
            @Override
            public void onResponse(Call<DBresult> call, Response<DBresult> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                } else if (response.code() == 404) {
                    Toast.makeText(LoginActivity.this, "Incorrect login credentials", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DBresult> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ── Navigation ──────────────────────────────────────────────
    private void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // ── Debug bypass ────────────────────────────────────────────
    private boolean canUseDebugBypassWithPassword(String email, String password) {
        return BuildConfig.DEBUG
                && !BuildConfig.DEBUG_LOGIN_EMAIL.isEmpty()
                && !BuildConfig.DEBUG_LOGIN_PASSWORD.isEmpty()
                && BuildConfig.DEBUG_LOGIN_EMAIL.equals(email)
                && BuildConfig.DEBUG_LOGIN_PASSWORD.equals(password);
    }

    private boolean canUseDebugBypassWithPin(String pin) {
        return BuildConfig.DEBUG
                && !BuildConfig.DEBUG_LOGIN_PIN.isEmpty()
                && BuildConfig.DEBUG_LOGIN_PIN.equals(pin);
    }

    // ── Lifecycle ───────────────────────────────────────────────
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseAccess != null) {
            databaseAccess.close();
        }
    }

    // ── Unused stubs kept for compatibility ─────────────────────
    private boolean isUserLoggedIn() {
        return false;
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}