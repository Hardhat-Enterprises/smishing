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
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smishingdetectionapp.BuildConfig;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.DataBase.VerifyBackupCodeResponse;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.databinding.ActivityLoginBinding;
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
import com.example.smishingdetectionapp.util.SessionManager;


public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private Retrofit retrofit;
    private Retrofitinterface retrofitinterface;
    private String BASE_URL = BuildConfig.SERVERIP;
    private boolean isPasswordVisible = false;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private boolean isPinLogin = false;  // Flag for PIN login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prevent screenshots/screen recording
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrofit setup
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitinterface = retrofit.create(Retrofitinterface.class);

        // Auto login check
        if (isUserLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        // ViewModel
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        // Bindings
        final EditText usernameEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.loginButton;
        final ProgressBar loadingProgressBar = binding.progressbar;
        final SignInButton googleBtn = binding.googleBtn;
        final Button registerButton = binding.registerButton;
        final ImageButton togglePasswordVisibility = binding.togglePasswordVisibility;
        final Button togglePinLogin = binding.togglePinLogin;
        final Button backupCodeButton = binding.loginWithBackupButton;

        // Toggle between password and PIN login
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
                loginButton.setText("Login with PIN");
                togglePinLogin.setText("Login with Password");
                isPinLogin = true;
            }
            passwordEditText.requestFocus();
        });

        // Normal login
        loginButton.setOnClickListener(v -> {
            String input = passwordEditText.getText().toString();
            if (isPinLogin) {
                if (input.length() != 6) {
                    passwordEditText.setError("PIN must be 6 digits");
                    return;
                }
                loginWithPin(input);
            } else {
                String email = usernameEditText.getText().toString();
                if (email.isEmpty() || input.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email and Password must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginWithPassword(email, input);
            }
        });

        // 🔹 Backup Code login with dialog
        backupCodeButton.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_backup_code, null);

            EditText emailInput = dialogView.findViewById(R.id.backupEmailInput);
            EditText codeInput = dialogView.findViewById(R.id.backupCodeInput);

            // Pre-fill email if already typed
            emailInput.setText(usernameEditText.getText().toString());

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Login with Backup Code")
                    .setView(dialogView)
                    .setPositiveButton("Verify", (dialog, which) -> {
                        String email = emailInput.getText().toString().trim();
                        String code = codeInput.getText().toString().trim();

                        if (email.isEmpty() || code.isEmpty()) {
                            Toast.makeText(this, "Both fields required", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        HashMap<String, String> map = new HashMap<>();
                        map.put("email", email);
                        map.put("code", code);

                        Call<VerifyBackupCodeResponse> call = retrofitinterface.verifyBackupCode(map);
                        call.enqueue(new Callback<VerifyBackupCodeResponse>() {
                            @Override
                            public void onResponse(Call<VerifyBackupCodeResponse> call,
                                                   Response<VerifyBackupCodeResponse> response) {
                                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                    SessionManager.saveEmail(LoginActivity.this, email);
                                    Toast.makeText(LoginActivity.this, "Backup code login successful!", Toast.LENGTH_SHORT).show();
                                    navigateToMainActivity();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid backup code", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<VerifyBackupCodeResponse> call, Throwable t) {
                                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Register navigation
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterMain.class));
            finish();
        });

        // Google Sign-In setup
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);
        gsc.signOut();

        googleBtn.setOnClickListener(v -> {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                signOutGoogle(this::signInGoogle);
            } else {
                signInGoogle();
            }
        });

        // Observe form state
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

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) return;
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);
            finish();
        });

        // Toggle password visibility
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

    // Google sign-in
    void signInGoogle() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    void signOutGoogle(Runnable onSignOutComplete) {
        gsc.signOut().addOnCompleteListener(task -> onSignOutComplete.run());
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
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginWithPin(String pin) {
        String email = binding.email.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO replace with real auth later
        SessionManager.saveEmail(this, email);
        Toast.makeText(LoginActivity.this, "PIN verified successfully (bypassed)", Toast.LENGTH_SHORT).show();
        navigateToMainActivity();
    }


    private void loginWithPassword(String email, String password) {
        SessionManager.saveEmail(this, email);
        Toast.makeText(LoginActivity.this, "Login successful (bypassed)", Toast.LENGTH_SHORT).show();
        navigateToMainActivity();
    }

    private boolean isUserLoggedIn() {
        return false;
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
    }
}
