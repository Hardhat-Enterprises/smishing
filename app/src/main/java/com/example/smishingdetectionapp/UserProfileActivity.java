package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tvName, tvPhone, tvAddress, tvAge, tvOccupation, tvEmail;
    private EditText etName, etPhone, etAddress, etAge, etOccupation, etEmail;
    private static final String PASSWORD = "pw12";
    private boolean isEditMode = false;
    private boolean isPasswordVerified = false;

    private Animation fadeIn;
    private Animation fadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Load animations
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);

        // Button to navigate to Risk Profile page
        Button riskProfileButton = findViewById(R.id.btn_risk_profile);
        riskProfileButton.startAnimation(slideIn); // Apply slide-in animation
        riskProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, RiskProfileActivity.class);
            startActivity(intent);
        });

        // Back button
        ImageButton report_back = findViewById(R.id.report_back);
        report_back.setOnClickListener(v -> {
            if (isEditMode) {
                // Toggle off edit mode before navigating back
                toggleEditMode();
            } else {
                try {
                    Intent intent = new Intent(UserProfileActivity.this, AccountActivity.class);
                    startActivity(intent);
                    finish(); // Finish the current activity
                } catch (Exception e) {
                    Toast.makeText(UserProfileActivity.this, "Error navigating back: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize TextView fields
        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
        tvAge = findViewById(R.id.tv_age);
        tvOccupation = findViewById(R.id.tv_occupation);
        tvEmail = findViewById(R.id.tv_email);

        // Initialize EditText fields
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etAge = findViewById(R.id.et_age);
        etOccupation = findViewById(R.id.et_occupation);
        etEmail = findViewById(R.id.et_email);

        // Initially mask sensitive data
        tvPhone.setText(getString(R.string.masked_data));
        tvAddress.setText(getString(R.string.masked_data));
        tvEmail.setText(getString(R.string.masked_data));

        // Set listeners for masked sensitive data fields
        View.OnClickListener sensitiveDataClickListener = view -> {
            if (!isPasswordVerified) {
                promptForPasswordToRevealSensitiveData();
            } else {
                revealAllSensitiveData();
            }
        };

        tvPhone.setOnClickListener(sensitiveDataClickListener);
        tvAddress.setOnClickListener(sensitiveDataClickListener);
        tvEmail.setOnClickListener(sensitiveDataClickListener);

        // Set listener for the "Edit Details" button
        findViewById(R.id.btn_edit_details).setOnClickListener(view -> {
            if (!isPasswordVerified) {
                promptForPasswordToEnableEditMode();
            } else {
                toggleEditMode();
            }
        });
    }

    private void toggleEditMode() {
        if (isEditMode) {
            // Save data from EditText to TextView
            tvName.setText(etName.getText().toString().trim());
            tvPhone.setText(etPhone.getText().toString().trim());
            tvAddress.setText(etAddress.getText().toString().trim());
            tvAge.setText(etAge.getText().toString().trim());
            tvOccupation.setText(etOccupation.getText().toString().trim());
            tvEmail.setText(etEmail.getText().toString().trim());

            // Hide EditText fields and show TextView fields
            etName.startAnimation(fadeOut);
            etName.setVisibility(View.GONE);
            etPhone.startAnimation(fadeOut);
            etPhone.setVisibility(View.GONE);
            etAddress.startAnimation(fadeOut);
            etAddress.setVisibility(View.GONE);
            etAge.startAnimation(fadeOut);
            etAge.setVisibility(View.GONE);
            etOccupation.startAnimation(fadeOut);
            etOccupation.setVisibility(View.GONE);
            etEmail.startAnimation(fadeOut);
            etEmail.setVisibility(View.GONE);

            tvName.startAnimation(fadeIn);
            tvName.setVisibility(View.VISIBLE);
            tvPhone.startAnimation(fadeIn);
            tvPhone.setVisibility(View.VISIBLE);
            tvAddress.startAnimation(fadeIn);
            tvAddress.setVisibility(View.VISIBLE);
            tvAge.startAnimation(fadeIn);
            tvAge.setVisibility(View.VISIBLE);
            tvOccupation.startAnimation(fadeIn);
            tvOccupation.setVisibility(View.VISIBLE);
            tvEmail.startAnimation(fadeIn);
            tvEmail.setVisibility(View.VISIBLE);

            // Update button text
            ((Button) findViewById(R.id.btn_edit_details)).setText(getString(R.string.edit_button));
            isEditMode = false;
        } else {
            // Populate EditText fields with current data from TextView
            etName.setText(tvName.getText().toString().trim());
            etPhone.setText(tvPhone.getText().toString().equals(getString(R.string.masked_data)) ? "" : tvPhone.getText().toString().trim());
            etAddress.setText(tvAddress.getText().toString().equals(getString(R.string.masked_data)) ? "" : tvAddress.getText().toString().trim());
            etAge.setText(tvAge.getText().toString().trim());
            etOccupation.setText(tvOccupation.getText().toString().trim());
            etEmail.setText(tvEmail.getText().toString().equals(getString(R.string.masked_data)) ? "" : tvEmail.getText().toString().trim());

            // Show EditText fields and hide TextView fields
            etName.startAnimation(fadeIn);
            etName.setVisibility(View.VISIBLE);
            etPhone.startAnimation(fadeIn);
            etPhone.setVisibility(View.VISIBLE);
            etAddress.startAnimation(fadeIn);
            etAddress.setVisibility(View.VISIBLE);
            etAge.startAnimation(fadeIn);
            etAge.setVisibility(View.VISIBLE);
            etOccupation.startAnimation(fadeIn);
            etOccupation.setVisibility(View.VISIBLE);
            etEmail.startAnimation(fadeIn);
            etEmail.setVisibility(View.VISIBLE);

            tvName.startAnimation(fadeOut);
            tvName.setVisibility(View.GONE);
            tvPhone.startAnimation(fadeOut);
            tvPhone.setVisibility(View.GONE);
            tvAddress.startAnimation(fadeOut);
            tvAddress.setVisibility(View.GONE);
            tvAge.startAnimation(fadeOut);
            tvAge.setVisibility(View.GONE);
            tvOccupation.startAnimation(fadeOut);
            tvOccupation.setVisibility(View.GONE);
            tvEmail.startAnimation(fadeOut);
            tvEmail.setVisibility(View.GONE);

            // Update button text
            ((Button) findViewById(R.id.btn_edit_details)).setText(getString(R.string.save_button));
            isEditMode = true;
        }
    }

    private void promptForPasswordToRevealSensitiveData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.enter_password_title));
        builder.setMessage(getString(R.string.enter_password_message));

        final TextInputEditText input = new TextInputEditText(this);
        input.setHint(getString(R.string.password_hint));
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.verify_password_message), (dialog, which) -> {
            String enteredPassword = input.getText() != null ? input.getText().toString() : "";
            if (enteredPassword.equals(PASSWORD)) {
                isPasswordVerified = true;
                revealAllSensitiveData();
                Toast.makeText(UserProfileActivity.this, getString(R.string.password_verified), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UserProfileActivity.this, getString(R.string.password_incorrect), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void promptForPasswordToEnableEditMode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.enter_password_title));
        builder.setMessage(getString(R.string.verify_password_message));

        final TextInputEditText input = new TextInputEditText(this);
        input.setHint(getString(R.string.password_hint));
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Verify", (dialog, which) -> {
            String enteredPassword = input.getText() != null ? input.getText().toString() : "";
            if (enteredPassword.equals(PASSWORD)) {
                isPasswordVerified = true;
                revealAllSensitiveData();
                toggleEditMode();
                Toast.makeText(UserProfileActivity.this, getString(R.string.edit_mode_enabled), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UserProfileActivity.this, getString(R.string.password_incorrect), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void revealAllSensitiveData() {
        tvPhone.setText(getString(R.string.secure_phone));
        tvAddress.setText(getString(R.string.secure_address));
        tvEmail.setText(getString(R.string.secure_email));
    }
}
