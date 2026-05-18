package com.example.smishingdetectionapp.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.text.InputType;
//import android.text.method.HideReturnsTransformationMethod;
//import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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

import java.util.HashMap;
import com.example.smishingdetectionapp.ui.login.ForgotPasswordActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private Retrofit retrofit;
    private Retrofitinterface retrofitinterface;
    private DatabaseAccess databaseAccess;
    //private Object BuildConfig;
    private String BASE_URL = BuildConfig.SERVERIP;
    private boolean isPasswordVisible = false;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private boolean isPinLogin = false;

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

        // BLOCKING screenshots and screen recording
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        // Inflate layout
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitinterface = retrofit.create(Retrofitinterface.class);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        final EditText usernameEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.loginButton;
        final ProgressBar loadingProgressBar = binding.progressbar;
        final SignInButton googleBtn = binding.googleBtn;
        final Button registerButton = binding.registerButton;
        final ImageButton togglePasswordVisibility = binding.togglePasswordVisibility;
        final Button togglePinLogin = binding.togglePinLogin;
        final TextView forgotPasswordButton = binding.forgotPasswordButton;

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

        loginButton.setOnClickListener(v -> {
            String input = passwordEditText.getText().toString();

            if (isPinLogin) {
                if (input.length() != 6) {
                    passwordEditText.setError("PIN must contains 6 digits");
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

        // Handle forgot password click
        forgotPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Handle register button click
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterMain.class));
            finish();
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        // Sign out of Google account to allow fresh authentication
        gsc.signOut().addOnCompleteListener(task -> {
            Toast.makeText(this, "Signed out successfully. You may sign in again.", Toast.LENGTH_SHORT).show();
        });

        // Handle Google Sign-In button click
        googleBtn.setOnClickListener(v -> {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                signOutGoogle(() -> signInGoogle());
            } else {
                signInGoogle();
            }
        });

        // Observe LoginFormState
        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) return;
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);
                finish();
            }
        });


        /*
        // Password visibility toggle
        togglePasswordVisibility.setOnClickListener(v -> {
            // Check the current input type to determine if the password is visible
            int currentInputType = passwordEditText.getInputType();

            if (currentInputType == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // If the password is currently hidden (password transformation is applied), show the password
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); // Show the password
                togglePasswordVisibility.setImageResource(R.drawable.visibility);  // Open eye icon
            } else {
                // If the password is currently visible, hide the password
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); // Hide the password
                togglePasswordVisibility.setImageResource(R.drawable.visibilityoff);  // Closed eye icon
            }

            // Move the cursor to the end
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

    }*/

        togglePasswordVisibility.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Hide password
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.visibilityoff); // lighter icon
                isPasswordVisible = false;
            } else {
                // Show password
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                togglePasswordVisibility.setImageResource(R.drawable.visibility); // darker icon
                isPasswordVisible = true;
            }

            // cursor stays at end of input
            passwordEditText.setSelection(passwordEditText.getText().length());
        });


    }
    //

    // Google Sign-In
    void signInGoogle() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    // Google Sign-Out
    void signOutGoogle(Runnable onSignOutComplete) {
        gsc.signOut().addOnCompleteListener(task -> {
            Toast.makeText(this, "Successfully signed out of Google account.", Toast.LENGTH_SHORT).show();
            onSignOutComplete.run();
        });
    }

    // Handle the result of the Google Sign-In
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
                if (response.code() == 200) {
                    navigateToMainActivity();
                } else if (response.code() == 404) {
                    Toast.makeText(LoginActivity.this, "Incorrect login Credentials", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DBresult> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ---------------- GOOGLE LOGIN ----------------
    private void signInGoogle() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
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
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ---------------- NAVIGATION ----------------
    private void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // ---------------- PLACEHOLDERS ----------------
    private boolean canUseDebugBypassWithPassword(String email, String password) {
        return false;
    }

    private boolean isUserLoggedIn() {
        return false;
    }

    private void updateUiWithUser(Object model) {}

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        // Reapply the secure flag when activity resumes
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
}
