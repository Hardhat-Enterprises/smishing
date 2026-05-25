// AccountActivity.java
package com.example.smishingdetectionapp.ui.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.BuildConfig;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.example.smishingdetectionapp.SharedActivity;
import com.example.smishingdetectionapp.UserProfileActivity;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountActivity extends SharedActivity {

    private Retrofitinterface retrofitinterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVERIP)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitinterface = retrofit.create(Retrofitinterface.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back to Settings
        ImageButton account_back = findViewById(R.id.account_back);
        account_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        Button viewProfileBtn = findViewById(R.id.viewProfileBtn);
        if (viewProfileBtn != null) {
            viewProfileBtn.setOnClickListener(v ->
                    startActivity(new Intent(this, UserProfileActivity.class))
            );
        }

        Button password_changeBtn = findViewById(R.id.passwordBtn);
        password_changeBtn.setOnClickListener(v -> {
            PopupPW bottomSheet = new PopupPW();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        Button name_changeBtn = findViewById(R.id.nameBtn);
        name_changeBtn.setOnClickListener(v -> {
            PopupName bottomSheet = new PopupName();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        Button pin_changeBtn = findViewById(R.id.pinBtn);
        pin_changeBtn.setOnClickListener(v -> {
            PopupPIN bottomSheet = new PopupPIN();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        Button email_changeBtn = findViewById(R.id.emailBtn);
        email_changeBtn.setOnClickListener(v -> {
            PopupEmail bottomSheet = new PopupEmail();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        Button sign_outBtn = findViewById(R.id.buttonSignOut);
        sign_outBtn.setOnClickListener(v -> {
            PopupSO bottomSheet = new PopupSO();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        Button deactivateBtn = findViewById(R.id.account_deactivate);
        if (deactivateBtn != null) {
            deactivateBtn.setOnClickListener(v -> showDeactivateConfirmation());
        }

        Button delete_accBtn = findViewById(R.id.account_delete);
        delete_accBtn.setOnClickListener(v -> {
            confirmDeletePopup confirmDeletePopup = new confirmDeletePopup(AccountActivity.this);
            confirmDeletePopup.show();
        });

        Button change_phone_numberBtn = findViewById(R.id.phoneBtn);
        change_phone_numberBtn.setOnClickListener(v -> {
            PopupPN bottomSheet = new PopupPN();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });
    }

    private void showDeactivateConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Deactivate Account")
                .setMessage("Are you sure you want to deactivate your account? You can reactivate it later by logging in.")
                .setPositiveButton("Deactivate", (dialog, which) -> performDeactivation())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performDeactivation() {
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", 0);
        String savedToken = prefs.getString("JWT_TOKEN", null);

        if (savedToken == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = "Bearer " + savedToken;

        retrofitinterface.deactivateAccount(token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AccountActivity.this, "Account deactivated successfully", Toast.LENGTH_LONG).show();
                    // Sign out user
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("JWT_TOKEN");
                    editor.apply();

                    Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Deactivation failed: " + response.code();
                    try (ResponseBody responseBody = response.errorBody()) {
                        if (responseBody != null) {
                            errorMsg = responseBody.string();
                        }
                    } catch (Exception e) {}
                    Toast.makeText(AccountActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(AccountActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
