package com.example.smishingdetectionapp.ui.account;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smishingdetectionapp.BuildConfig;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PopupEmail extends BottomSheetDialogFragment {

    private EditText etNewEmail, etConfirmEmail;
    private Button btnUpdate;
    private Retrofitinterface retrofitinterface;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popup_email,
                container, false);

        // Bind UI
        etNewEmail = v.findViewById(R.id.editTextEmail);
        etConfirmEmail = v.findViewById(R.id.editTextEmail2);
        btnUpdate = v.findViewById(R.id.change_emailBtn);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVERIP)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitinterface = retrofit.create(Retrofitinterface.class);

        btnUpdate.setEnabled(false);

        // TextWatcher to enable button only when emails match
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validate();
            }
        };

        etNewEmail.addTextChangedListener(watcher);
        etConfirmEmail.addTextChangedListener(watcher);

        // Button click → call API
        btnUpdate.setOnClickListener(view -> {
            String email = etNewEmail.getText().toString().trim();
            callUpdateAPI(email);
        });

        return v;
    }

    private void validate() {
        String email = etNewEmail.getText().toString().trim();
        String confirm = etConfirmEmail.getText().toString().trim();

        boolean isValidEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches();
        boolean isMatching = email.equals(confirm);

        if (!isValidEmail && !email.isEmpty()) {
            etNewEmail.setError("Invalid email address format");
        }
        if (!isMatching && !confirm.isEmpty()) {
            etConfirmEmail.setError("Emails must match");
        }

        btnUpdate.setEnabled(isValidEmail && isMatching && !email.isEmpty());
    }

    // ================= API CALL =================
    private void callUpdateAPI(String email) {
        SharedPreferences prefs = getActivity().getSharedPreferences("APP_PREFS", 0);
        String savedToken = prefs.getString("JWT_TOKEN", null);

        if (savedToken == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = "Bearer " + savedToken;

        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);

        retrofitinterface.updateProfile(token, map).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Email updated successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(getContext(), "Error " + response.code() + ": " + errorBody, Toast.LENGTH_LONG).show();

                        // Show error message for debugging, delete before app launches
                        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                                .setTitle("Update Error " + response.code())
                                .setMessage(errorBody)
                                .setPositiveButton("OK", null)
                                .show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
