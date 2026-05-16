package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.chat.ChatAssistantActivity;
import com.example.smishingdetectionapp.ui.account.AccountActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class SettingsActivity extends AppCompatActivity {
    private SeekBar seekBarFontScale;
    private TextView preview;
    private static final int TIMEOUT_MILLIS = 10000;
    private boolean isAuthenticated = false;
    private BiometricPrompt biometricPrompt;
    private Button buttonIncreaseTextSize, buttonDecreaseTextSize, dialogCancel, dialogSignout;
    private TextView textScaleLabel;
    private float textScale;
    private Dialog dialog;
    private static final String KEY_SCROLL_POSITION = "scroll_position";
    private int savedPosition = 0;
    private ScrollView scrollView;
    private SharedPreferences prefs;
    private boolean isColdStart = true;
    private Switch darkModeSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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

        textScaleLabel = findViewById(R.id.textScaleLabel);
        seekBarFontScale = findViewById(R.id.seekBarFontScale);
        textScale = PreferencesUtil.getTextScale(this);
        updateScaleLabel();

        seekBarFontScale.setProgress((int) (textScale * 10));

        seekBarFontScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float newScale = progress / 10f;
                if (newScale < 0.8f) newScale = 0.8f;
                if (newScale > 1.5f) newScale = 1.5f;
                textScale = newScale;
                PreferencesUtil.setTextScale(SettingsActivity.this, textScale);
                updateScaleLabel();
                applyFontScale();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        if (isBold) {
            applyBoldToAllSwitches(findViewById(R.id.settingsScroll));
        }
        if (isBold) {
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

        // Bold Text switch
        Switch boldSwitch = findViewById(R.id.bold_text);
        if (boldSwitch != null) {
            boldSwitch.setChecked(isBold);
            boldSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                saveScrollPosition();
                prefs.edit().putBoolean("bold_text_enabled", isChecked).apply();
                recreate();
            });
        }

        // Always Underline Links switch
        Switch underlineSwitch = findViewById(R.id.always_underline_links);
        if (underlineSwitch != null) {
            boolean isUnderline = prefs.getBoolean("always_underline_links", false);
            underlineSwitch.setChecked(isUnderline);
            underlineSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                saveScrollPosition();
                prefs.edit().putBoolean("always_underline_links", isChecked).apply();
            });
        }

        BottomNavCoordinator.setup(this, R.id.nav_settings);

        // Account button to switch to account page with biometric authentication
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_settings);

        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                return true;
            }
            return false;
        });

        // Account button
        Button accountBtn = findViewById(R.id.accountBtn);
        accountBtn.setOnClickListener(v -> openAccountActivity());

        // Notification button to switch to notification page
        Button notificationBtn = findViewById(R.id.notificationBtn);
        notificationBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });

        // Filtering button to switch to Smishing rules page
        ImageView filteringBtn = findViewById(R.id.imageView7);
        if (filteringBtn != null) {
            filteringBtn.setOnClickListener(v -> {
                startActivity(new Intent(this, SmishingRulesActivity.class));
            });
        }

        // Report button to switch to reporting page
        // Filtering button
        Button filteringBtn = findViewById(R.id.filteringBtn);
        filteringBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, SmishingRulesActivity.class));
            finish();
        });

        // Report button
        Button reportBtn = findViewById(R.id.reportBtn);
        reportBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ReportingActivity.class));
            finish();
        });

        // Help button to switch to Help page
        // Help button
        Button helpBtn = findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, HelpActivity.class));
            finish();
        });

        // About Me button
        Button aboutMeButton = findViewById(R.id.aboutMeBtn);
        aboutMeButton.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, AboutMeActivity.class));
        });

        // About Us button
        Button aboutUsBtn = findViewById(R.id.aboutUsBtn);
        aboutUsBtn.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, AboutUsActivity.class));
        });

        // Chat Assistant button
        Button chatAssistantBtn = findViewById(R.id.chatAssistantBtn);
        chatAssistantBtn.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, ChatAssistantActivity.class));
        });

        // Feedback Button to switch to Feedback page
        Button feedbackBtn = findViewById(R.id.feedbackBtn);
        feedbackBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, FeedbackActivity.class));
        });

        // Community Button to switch to Community page
        Button communityBtn = findViewById(R.id.communityBtn);
        communityBtn.setOnClickListener(v -> {
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

        MaterialButton inviteFriendsBtn = findViewById(R.id.inviteFriendsBtn);
        inviteFriendsBtn.setOnClickListener(v -> {
            String playLink = "https://play.google.com/store/apps/details?id=" + getPackageName();
            String msg = "Stay safe from smishing! Try the Smishing Detection app:\n" + playLink;
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, "Join me on Smishing Detection");
            share.putExtra(Intent.EXTRA_TEXT, msg);
            startActivity(Intent.createChooser(share, "Invite a Friend"));
        });
    }

    private void triggerBiometricAuthenticationWithTimeout() {
        int authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG
                | BiometricManager.Authenticators.DEVICE_CREDENTIAL;

        BiometricManager bm = BiometricManager.from(this);
        switch (bm.canAuthenticate(authenticators)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                biometricPrompt = getPrompt();
                biometricPrompt.authenticate(buildPromptInfo(authenticators));
                startTimeoutTimer();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                openAccountActivity();
                break;
            default:
                notifyUser("Biometric authentication unavailable");
                openAccountActivity();
                break;
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
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser("Authentication Error: " + errString);
                redirectToSettingsActivity();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                notifyUser("Authentication Succeeded!");
                isAuthenticated = true;
                openAccountActivity();
            }

            @Override
            public void onAuthenticationFailed() {
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

    public void openNotificationsActivity(View view) {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
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
        Configuration configuration = getResources().getConfiguration();
        configuration = new Configuration(configuration);
        configuration.fontScale = textScale;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        recreate();
    }

    private void saveAndApplyScale() {
        PreferencesUtil.setTextScale(this, textScale);
        updateScaleLabel();
        applyFontScale();
    }

    private void updateScaleLabel() {
        int percentage = (int) (textScale * 100);
        textScaleLabel.setText(percentage + "%");
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        // Feedback button
        Button feedbackBtn = findViewById(R.id.feedbackBtn);
        feedbackBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, FeedbackActivity.class));
            finish();
        });

        // Forum button
        Button forumBtn = findViewById(R.id.forumBtn);
        forumBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ForumActivity.class));
            finish();
        });
    }

    // Open AccountActivity directly
    private void openAccountActivity() {
        startActivity(new Intent(SettingsActivity.this, AccountActivity.class));
        finish();
    }

    private void restoreScrollPosition() {
        savedPosition = prefs.getInt("scroll_pos", 0);
        if (isTaskRoot()) {
            savedPosition = 0;
        }
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
    // Notification button
    public void openNotificationsActivity(View view) {
        startActivity(new Intent(this, NotificationActivity.class));
    }
}
