package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class UserProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_PIN = "user_pin";
    private static final String KEY_UNLOCK_TIME = "unlock_time";

    private static final String DEFAULT_PIN = "1234"; // default PIN
    private static final long UNLOCK_DURATION = 30 * 1000; // 30s unlock

    private EditText etName, etEmail, etPhone, etAddress;
    private ImageButton eyeEmail, eyePhone, eyeAddress, backBtn;
    private MaterialButton editBtn, resetPinBtn;

    private boolean isEditMode = false;
    private boolean unlocked = false;
    private long unlockTimestamp = 0;

    // Demo user data
    private String actualName = "Moshadi Hansamali";
    private String actualEmail = "moshadi@gmail.com";
    private String actualPhone = "0489300074";
    private String actualAddress = "24 Rhynhurst Street, Clyde North";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Fields
        etName = findViewById(R.id.et_value_name);
        etEmail = findViewById(R.id.et_value_email);
        etPhone = findViewById(R.id.et_value_phone);
        etAddress = findViewById(R.id.et_value_address);

        // Eye buttons
        eyeEmail = findViewById(R.id.btn_eye_email);
        eyePhone = findViewById(R.id.btn_eye_phone);
        eyeAddress = findViewById(R.id.btn_eye_address);

        // Back button
        backBtn = findViewById(R.id.profile_back);
        backBtn.setOnClickListener(v -> finish());

        // Buttons
        editBtn = findViewById(R.id.btn_edit_profile);
        resetPinBtn = findViewById(R.id.btn_reset_pin);

        // Mask initially
        maskFields();

        // Restore unlock status if still valid
        unlockTimestamp = getUnlockTimestamp();
        if (unlockTimestamp > 0 && !isUnlockExpired()) {
            unlocked = true;
            unlockAllFields();
        }

        // Eye button → reveal single field
        eyeEmail.setOnClickListener(v -> {
            if (unlocked && !isUnlockExpired()) {
                unlockEmailField();
            } else {
                unlocked = false;
                showPinDialog(this::unlockEmailField);
            }
        });

        eyePhone.setOnClickListener(v -> {
            if (unlocked && !isUnlockExpired()) {
                unlockPhoneField();
            } else {
                unlocked = false;
                showPinDialog(this::unlockPhoneField);
            }
        });

        eyeAddress.setOnClickListener(v -> {
            if (unlocked && !isUnlockExpired()) {
                unlockAddressField();
            } else {
                unlocked = false;
                showPinDialog(this::unlockAddressField);
            }
        });

        // Edit profile → needs PIN
        editBtn.setOnClickListener(v -> {
            if (!isEditMode) {
                if (unlocked && !isUnlockExpired()) {
                    unlockAllFields();
                    enableEditMode(true);
                } else {
                    unlocked = false;
                    showPinDialog(() -> {
                        unlockAllFields();
                        enableEditMode(true);
                    });
                }
            } else {
                enableEditMode(false);
            }
        });

        // Reset PIN
        resetPinBtn.setOnClickListener(v -> showResetPinDialog());

        // Risk profile navigation
        MaterialButton viewRiskBtn = findViewById(R.id.btn_view_risk);
        if (viewRiskBtn != null) {
            viewRiskBtn.setOnClickListener(v -> {
                try {
                    Class<?> cls = Class.forName("com.example.smishingdetectionapp.RiskProfileActivity");
                    startActivity(new Intent(this, cls));
                } catch (Exception e) {
                    Toast.makeText(this, "Risk Profile not ready", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // === Field helpers ===
    private void maskFields() {
        etEmail.setText("••••••••");
        etPhone.setText("••••••••");
        etAddress.setText("••••••••");
        etName.setText(actualName);
        setFieldsEditable(false);
    }

    private void unlockAllFields() {
        etName.setText(actualName);
        etEmail.setText(actualEmail);
        etPhone.setText(actualPhone);
        etAddress.setText(actualAddress);
    }

    private void unlockEmailField() {
        etEmail.setText(actualEmail);
    }

    private void unlockPhoneField() {
        etPhone.setText(actualPhone);
    }

    private void unlockAddressField() {
        etAddress.setText(actualAddress);
    }

    private void enableEditMode(boolean enable) {
        if (enable) {
            isEditMode = true;
            editBtn.setText(getString(R.string.save_button));
            setFieldsEditable(true);
        } else {
            isEditMode = false;
            editBtn.setText(getString(R.string.edit_button));

            // Save updates
            actualName = etName.getText().toString();
            actualEmail = etEmail.getText().toString();
            actualPhone = etPhone.getText().toString();
            actualAddress = etAddress.getText().toString();

            setFieldsEditable(false);
            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setFieldsEditable(boolean editable) {
        etName.setEnabled(editable);
        etEmail.setEnabled(editable);
        etPhone.setEnabled(editable);
        etAddress.setEnabled(editable);
    }

    // === PIN dialogs ===
    private void showPinDialog(Runnable onSuccess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter PIN");

        String storedPin = getUserPin();
        if (storedPin == null) {
            builder.setMessage("First time? Enter a 4-digit PIN to set it, or use the default: " + DEFAULT_PIN);
        }

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String currentStoredPin = getUserPin();
            String enteredPin = input.getText().toString().trim();

            if (currentStoredPin == null) {
                if (enteredPin.equals(DEFAULT_PIN)) {
                    saveUserPin(DEFAULT_PIN);
                    unlocked = true;
                    unlockTimestamp = System.currentTimeMillis();
                    saveUnlockTimestamp(unlockTimestamp);
                    Toast.makeText(this, "Default PIN set. Please reset soon.", Toast.LENGTH_SHORT).show();
                    onSuccess.run();
                } else if (enteredPin.length() == 4) {
                    saveUserPin(enteredPin);
                    unlocked = true;
                    unlockTimestamp = System.currentTimeMillis();
                    saveUnlockTimestamp(unlockTimestamp);
                    Toast.makeText(this, "PIN set successfully!", Toast.LENGTH_SHORT).show();
                    onSuccess.run();
                } else {
                    Toast.makeText(this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (enteredPin.equals(currentStoredPin)) {
                    unlocked = true;
                    unlockTimestamp = System.currentTimeMillis();
                    saveUnlockTimestamp(unlockTimestamp);
                    Toast.makeText(this, "Unlocked for 30 seconds", Toast.LENGTH_SHORT).show();
                    onSuccess.run();
                } else {
                    Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showResetPinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset PIN");

        final EditText emailInput = new EditText(this);
        emailInput.setHint("Enter your email to confirm");
        emailInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(emailInput);

        builder.setPositiveButton("Next", (dialog, which) -> {
            String enteredEmail = emailInput.getText().toString().trim();
            if (enteredEmail.equals(actualEmail)) {
                showNewPinDialog();
            } else {
                Toast.makeText(this, "Email does not match!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showNewPinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set New PIN");

        final EditText pinInput = new EditText(this);
        pinInput.setHint("Enter new 4-digit PIN");
        pinInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(pinInput);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newPin = pinInput.getText().toString().trim();
            if (newPin.length() == 4) {
                saveUserPin(newPin);
                unlocked = false;
                saveUnlockTimestamp(0);
                Toast.makeText(this, "PIN reset successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // === Unlock timer ===
    private boolean isUnlockExpired() {
        return System.currentTimeMillis() - unlockTimestamp > UNLOCK_DURATION;
    }

    // === SharedPreferences helpers ===
    private void saveUserPin(String pin) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_PIN, pin)
                .apply();
    }

    private String getUserPin() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(KEY_PIN, null);
    }

    private void saveUnlockTimestamp(long time) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putLong(KEY_UNLOCK_TIME, time)
                .apply();
    }

    private long getUnlockTimestamp() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getLong(KEY_UNLOCK_TIME, 0);
    }
}
