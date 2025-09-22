package com.example.smishingdetectionapp;

import android.app.Dialog;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.Configuration;
import android.content.ClipboardManager;
import android.content.ClipData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.smishingdetectionapp.Community.CommunityHomeActivity;
import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.chat.ChatAssistantActivity;
import com.example.smishingdetectionapp.ui.account.AccountActivity;
import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.Executor;

import android.widget.ScrollView;
import android.graphics.Typeface;
import android.view.ViewGroup;

import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.widget.Switch;
import com.example.smishingdetectionapp.ui.ContactUsActivity;

// NEW imports
import com.example.smishingdetectionapp.data.LoginRepository;
import com.example.smishingdetectionapp.data.Result;
import com.example.smishingdetectionapp.DataBase.BackupCodesResponse;
import com.example.smishingdetectionapp.util.SessionManager;


public class SettingsActivity extends AppCompatActivity {
    private SeekBar seekBarFontScale;
    private static final int TIMEOUT_MILLIS = 10000;
    private boolean isAuthenticated = false;
    private BiometricPrompt biometricPrompt;
    private Button dialogCancel, dialogSignout;
    private TextView textScaleLabel;
    private float textScale;
    private Dialog dialog;
    private static final String KEY_SCROLL_POSITION = "scroll_position";
    private int savedPosition = 0;
    private ScrollView scrollView;
    private SharedPreferences prefs;
    private Switch darkModeSwitch;

    // NEW fields for backup codes
    private Button generateBackupCodesBtn;
    private LoginRepository loginRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isBold = prefs.getBoolean("bold_text_enabled", false);
        setTheme(isBold ? R.style.Theme_SmishingDetectionApp_Bold : R.style.Theme_SmishingDetectionApp);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // --------- Dark Mode ----------
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        darkModeSwitch.setChecked(isDarkMode);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            recreate();
        });

        // --------- Font Scaling ----------
        textScaleLabel = findViewById(R.id.textScaleLabel);
        seekBarFontScale = findViewById(R.id.seekBarFontScale);
        textScale = PreferencesUtil.getTextScale(this);
        updateScaleLabel();
        seekBarFontScale.setProgress((int) (textScale * 10));
        seekBarFontScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float newScale = progress / 10f;
                if (newScale < 0.8f) newScale = 0.8f;
                if (newScale > 1.5f) newScale = 1.5f;
                textScale = newScale;
                PreferencesUtil.setTextScale(SettingsActivity.this, textScale);
                updateScaleLabel();
                applyFontScale();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        if (isBold) {
            applyBoldToAllSwitches(findViewById(R.id.settingsScroll));
            applyBoldToAllWidgets(findViewById(R.id.settingsScroll));
        }
        scrollView = findViewById(R.id.settingsScroll);

        boolean isFromNav = getIntent().getBooleanExtra("from_navigation", false);
        boolean isCold = prefs.getBoolean("cold_start", true);
        if (isFromNav || isCold) {
            scrollView.post(() -> scrollView.scrollTo(0, 0));
            prefs.edit().putBoolean("cold_start", false).apply();
        } else {
            restoreScrollPosition();
        }

        Switch boldSwitch = findViewById(R.id.bold_text);
        if (boldSwitch != null) {
            boldSwitch.setChecked(isBold);
            boldSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                saveScrollPosition();
                prefs.edit().putBoolean("bold_text_enabled", isChecked).apply();
                recreate();
            });
        }

        // --------- Bottom Nav ----------
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_settings);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (id == R.id.nav_report) {
                Intent i = new Intent(this, CommunityReportActivity.class);
                i.putExtra("source", "home");
                startActivity(i);
                overridePendingTransition(0,0);
                finish(); return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra("from_navigation", true);
                startActivity(intent);
                overridePendingTransition(0, 0); finish(); return true;
            }
            return false;
        });

        // --------- Other Buttons ----------
        findViewById(R.id.accountBtn).setOnClickListener(v -> triggerBiometricAuthenticationWithTimeout());
        findViewById(R.id.notificationBtn).setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));
        findViewById(R.id.imageView7).setOnClickListener(v -> startActivity(new Intent(this, SmishingRulesActivity.class)));
        findViewById(R.id.reportBtn).setOnClickListener(v -> startActivity(new Intent(this, CommunityReportActivity.class)));
        findViewById(R.id.helpBtn).setOnClickListener(v -> startActivity(new Intent(this, HelpActivity.class)));
        findViewById(R.id.aboutMeBtn).setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, AboutMeActivity.class)));
        findViewById(R.id.aboutUsBtn).setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, AboutUsActivity.class)));
        findViewById(R.id.contactUsBtn).setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, ContactUsActivity.class)));
        findViewById(R.id.chatAssistantBtn).setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, ChatAssistantActivity.class)));
        findViewById(R.id.feedbackBtn).setOnClickListener(v -> startActivity(new Intent(this, FeedbackActivity.class)));
        findViewById(R.id.communityBtn).setOnClickListener(v -> {
            Intent i = new Intent(this, CommunityHomeActivity.class);
            i.putExtra("source", "settings");
            startActivity(i);
        });

        Button signoutBtn = findViewById(R.id.buttonSignOut);
        Intent intent = new Intent(this, LoginActivity.class);
        dialog = new Dialog(SettingsActivity.this);
        dialog.setContentView(R.layout.dialog_signout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogCancel = dialog.findViewById(R.id.signoutCancelBtn);
        dialogSignout = dialog.findViewById(R.id.signoutBtn);
        dialogCancel.setOnClickListener(v -> dialog.dismiss());
        dialogSignout.setOnClickListener(v -> {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        signoutBtn.setOnClickListener(v -> dialog.show());

        if (isTaskRoot()) {
            prefs.edit().putBoolean("cold_start", true).apply();
            prefs.edit().remove("scroll_pos").apply();
        }

        findViewById(R.id.inviteFriendsBtn).setOnClickListener(v -> {
            String playLink = "https://play.google.com/store/apps/details?id=" + getPackageName();
            String msg = "Stay safe from smishing! Try the Smishing Detection app:\n" + playLink;
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, "Join me on Smishing Detection");
            share.putExtra(Intent.EXTRA_TEXT, msg);
            startActivity(Intent.createChooser(share, "Invite a Friend"));
        });

        // --------- BACKUP CODES FEATURE ----------
        loginRepository = LoginRepository.getInstance(new com.example.smishingdetectionapp.data.LoginDataSource());
        generateBackupCodesBtn = findViewById(R.id.generateBackupCodesBtn);

        if (generateBackupCodesBtn != null) {
            generateBackupCodesBtn.setOnClickListener(v -> {
                String email = SessionManager.getEmail(this);
                if (email == null || email.isEmpty()) {
                    Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginRepository.generateBackupCodes(email, result -> {
                    runOnUiThread(() -> {
                        if (result instanceof Result.Success) {
                            BackupCodesResponse response = ((Result.Success<BackupCodesResponse>) result).getData();
                            if (response != null && response.getCodes() != null && !response.getCodes().isEmpty()) {
                                showBackupCodesDialog(response.getCodes());
                            } else {
                                Toast.makeText(this, "No codes received", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Failed to generate backup codes", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            });
        }
    }

    // ---------- Show Dialog with Backup Codes ----------
    private void showBackupCodesDialog(java.util.List<String> codes) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_backup_codes);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView codesText = dialog.findViewById(R.id.backupCodesText);
        Button copyBtn = dialog.findViewById(R.id.copyBackupCodesBtn);
        Button closeBtn = dialog.findViewById(R.id.closeDialogBtn);

        StringBuilder builder = new StringBuilder();
        for (String code : codes) {
            builder.append(code).append("\n");
        }
        codesText.setText(builder.toString().trim());

        copyBtn.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Backup Codes", codesText.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Codes copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        closeBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // ---------- BIOMETRIC + UTILITIES ----------
    private void triggerBiometricAuthenticationWithTimeout() {
        int authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG
                | BiometricManager.Authenticators.DEVICE_CREDENTIAL;
        BiometricManager bm = BiometricManager.from(this);
        switch (bm.canAuthenticate(authenticators)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                biometricPrompt = getPrompt();
                biometricPrompt.authenticate(buildPromptInfo(authenticators));
                startTimeoutTimer(); break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                openAccountActivity(); break;
            default:
                notifyUser("Biometric authentication unavailable");
                openAccountActivity(); break;
        }
    }

    private BiometricPrompt.PromptInfo buildPromptInfo(int authenticators) {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentication Required")
                .setDescription("Please authenticate to access your account settings")
                .setAllowedAuthenticators(authenticators)
                .build();
    }

    private BiometricPrompt getPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser("Authentication Error: " + errString);
                redirectToSettingsActivity();
            }
            @Override public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                notifyUser("Authentication Succeeded!");
                isAuthenticated = true;
                openAccountActivity();
            }
            @Override public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                notifyUser("Authentication Failed");
            }
        };
        return new BiometricPrompt(this, executor, callback);
    }

    private void startTimeoutTimer() {
        new Handler().postDelayed(() -> {
            if (!isAuthenticated) {
                notifyUser("Authentication timed out. Redirecting to Settings...");
                biometricPrompt.cancelAuthentication();
                redirectToSettingsActivity();
            }
        }, TIMEOUT_MILLIS);
    }

    private void redirectToSettingsActivity() {
        Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    private void openAccountActivity() {
        Intent intent = new Intent(SettingsActivity.this, AccountActivity.class);
        startActivity(intent);
        finish();
    }

    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void applyBoldToAllSwitches(View root) {
        if (!(root instanceof ViewGroup)) return;
        ViewGroup group = (ViewGroup) root;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof android.widget.Switch || child instanceof androidx.appcompat.widget.SwitchCompat) {
                ((TextView) child).setTypeface(null, Typeface.BOLD);
            }
            applyBoldToAllSwitches(child);
        }
    }

    private void applyBoldToAllWidgets(View root) {
        if (!(root instanceof ViewGroup)) return;
        ViewGroup group = (ViewGroup) root;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof android.widget.Switch || child instanceof androidx.appcompat.widget.SwitchCompat) {
                ((TextView) child).setTypeface(null, Typeface.BOLD);
            }
            if (child instanceof android.widget.Button ||
                    child instanceof com.google.android.material.button.MaterialButton) {
                ((TextView) child).setTypeface(null, Typeface.BOLD);
            }
            applyBoldToAllWidgets(child);
        }
    }

    private void applyFontScale() {
        Configuration configuration = new Configuration(getResources().getConfiguration());
        configuration.fontScale = textScale;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        recreate();
    }

    private void updateScaleLabel() {
        int percentage = (int) (textScale * 100);
        textScaleLabel.setText(percentage + "%");
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        finish();
        super.onBackPressed();
    }

    private void saveScrollPosition() {
        if (scrollView != null) {
            int scrollY = scrollView.getScrollY();
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putInt("scroll_pos", scrollY)
                    .apply();
        }
    }

    private void restoreScrollPosition() {
        savedPosition = prefs.getInt("scroll_pos", 0);
        if (isTaskRoot()) savedPosition = 0;
        scrollView.post(() -> scrollView.scrollTo(0, savedPosition));
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveScrollPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!prefs.getBoolean("cold_start", false)) {
            restoreScrollPosition();
        }
    }
}
