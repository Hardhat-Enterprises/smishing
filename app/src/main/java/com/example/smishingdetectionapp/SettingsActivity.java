// Inside your onCreate() method:

// Account button to trigger biometric authentication with timeout
Button accountBtn = findViewById(R.id.accountBtn);
accountBtn.setOnClickListener(v -> triggerBiometricAuthenticationWithTimeout());

// Filtering button to switch to Smishing rules page
Button filteringBtn = findViewById(R.id.filteringBtn);
filteringBtn.setOnClickListener(v -> {
    startActivity(new Intent(this, SmishingRulesActivity.class));
    // finish(); // Uncomment if you want to finish the current activity
});

// Report button to switch to reporting page
Button reportBtn = findViewById(R.id.reportBtn);
reportBtn.setOnClickListener(v -> {
    startActivity(new Intent(this, ReportingActivity.class));
    // finish(); // Uncomment if you want to finish the current activity
});

// Notification button to switch to notification page
Button notificationsButton = findViewById(R.id.notificationsBtn);
notificationsButton.setOnClickListener(v -> {
    startActivity(new Intent(this, NotificationActivity.class));
    // finish(); // Uncomment if you want to finish the current activity
});

// Help button to switch to Help page
Button helpBtn = findViewById(R.id.helpBtn);
helpBtn.setOnClickListener(v -> {
    startActivity(new Intent(this, HelpActivity.class));
    // finish(); // Uncomment if you want to finish the current activity
});

// About Me button to switch to AboutMeActivity
Button aboutMeButton = findViewById(R.id.aboutMeBtn);
aboutMeButton.setOnClickListener(v -> {
    Intent intent = new Intent(SettingsActivity.this, AboutMeActivity.class);
    startActivity(intent);
});

// About Us button to switch to AboutUsActivity
Button aboutUsBtn = findViewById(R.id.aboutUsBtn);
aboutUsBtn.setOnClickListener(v -> {
    Intent intent = new Intent(SettingsActivity.this, AboutUsActivity.class);
    startActivity(intent);
});

// Chat Assistant button to switch to ChatAssistantActivity
Button chatAssistantBtn = findViewById(R.id.chatAssistantBtn);
chatAssistantBtn.setOnClickListener(v -> {
    Intent intent = new Intent(SettingsActivity.this, ChatAssistantActivity.class);
    startActivity(intent);
});

// Feedback button to switch to FeedbackActivity
Button feedbackBtn = findViewById(R.id.feedbackBtn);
feedbackBtn.setOnClickListener(v -> {
    startActivity(new Intent(this, FeedbackActivity.class));
    finish();
});

// Forum button to switch to ForumActivity
Button forumBtn = findViewById(R.id.forumBtn);
forumBtn.setOnClickListener(v -> {
    startActivity(new Intent(this, ForumActivity.class));
    finish();
});

// Notification button to switch to NotificationActivity (alternate implementation)
@SuppressWarnings("unused")
public void openNotificationsActivity(View view) {
    Intent intent = new Intent(this, NotificationActivity.class);
    startActivity(intent);
}

// Trigger biometric authentication with timeout
private void triggerBiometricAuthenticationWithTimeout() {
    BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authentication Required")
            .setDescription("Please authenticate to access your account settings")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build();

    // Start the authentication process
    biometricPrompt = getPrompt();
    biometricPrompt.authenticate(promptInfo);

    // Start the timeout timer
    startTimeoutTimer();
}

// BiometricPrompt setup
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
            isAuthenticated = true; // Mark as authenticated
            openAccountActivity(); // Proceed to AccountActivity
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            notifyUser("Authentication Failed");
        }
    };

    return new BiometricPrompt(this, executor, callback);
}

// Start a timeout timer for authentication
private void startTimeoutTimer() {
    new Handler().postDelayed(() -> {
        if (!isAuthenticated) { // If authentication hasn't occurred within the timeout
            notifyUser("Authentication timed out. Redirecting to Settings...");
            biometricPrompt.cancelAuthentication(); // Cancel the ongoing authentication
            redirectToSettingsActivity(); // Redirect to SettingsActivity on timeout
        }
    }, TIMEOUT_MILLIS);
}

// Redirect to SettingsActivity
private void redirectToSettingsActivity() {
    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
    startActivity(intent);
    finish(); // Ensure the current activity is closed
}

// Open AccountActivity
private void openAccountActivity() {
    Intent intent = new Intent(SettingsActivity.this, AccountActivity.class);
    startActivity(intent);
    finish(); // Close SettingsActivity if AccountActivity is opened
}

// Show a toast message
private void notifyUser(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
}

