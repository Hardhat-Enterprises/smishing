package com.example.smishingdetectionapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smishingdetectionapp.databinding.ActivityMainBinding;
import com.example.smishingdetectionapp.notifications.NotificationPermissionDialogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_CODE = 101;
    private AppBarConfiguration mAppBarConfiguration;
    private TextView total_count;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup AppBarConfiguration for Navigation
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_news, R.id.nav_settings)
                .build();

        // Check and request necessary permissions
        checkAndRequestPermissions();

        // Check if notifications are enabled, otherwise show permission dialog
        if (!areNotificationsEnabled()) {
            showNotificationPermissionDialog();
        }

        // Initialize Bottom Navigation
        setupBottomNavigation();

        // Debug button to navigate to DebugActivity
        Button debug_btn = findViewById(R.id.debug_btn);
        debug_btn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DebugActivity.class)));

        // Detections button to navigate to DetectionsActivity
        Button detections_btn = findViewById(R.id.detections_btn);
        detections_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, DetectionsActivity.class));
            finish();
        });

        // Access the database and display total count in TextView
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        TextView total_count = findViewById(R.id.total_counter);
        total_count.setText("" + databaseAccess.getCounter());
        databaseAccess.close();

        // Encryption code integration
        handleEncryptedSharedPreferences();
    }

    // Setup bottom navigation with appropriate listeners
    private void setupBottomNavigation() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    // Encryption handling for storing sensitive data
    private void handleEncryptedSharedPreferences() {
        try {
            SharedPreferences encryptedPrefs = EncryptionUtil.INSTANCE.getEncryptedSharedPreferences(this);

            // Example of storing an encrypted value
            String sensitiveData = "sensitive_value";
            encryptedPrefs.edit().putString("key", sensitiveData).apply();
            Log.d("MainActivity", "Encrypted value stored in SharedPreferences.");

            // Example of retrieving and automatically decrypting the value
            String decryptedValue = encryptedPrefs.getString("key", "default_value");
            Log.d("MainActivity", "Decrypted SharedPreferences value: " + decryptedValue);

            // Show a Toast with the decrypted value
            Toast.makeText(this, "Decrypted Value: " + decryptedValue, Toast.LENGTH_SHORT).show();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            Log.e("MainActivity", "Error encrypting SharedPreferences", e);
        }
    }

    // Check and request necessary permissions for SMS and notifications
    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Build.VERSION.SDK_INT >= 33 ? Manifest.permission.POST_NOTIFICATIONS : ""
        };

        // Filter permissions that haven't been granted
        String[] permissionsToRequest = java.util.Arrays.stream(permissions)
                .filter(permission -> !permission.isEmpty() && ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                .toArray(String[]::new);

        if (permissionsToRequest.length > 0) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && allPermissionsGranted(grantResults)) {
                // Permissions granted
                Log.d("MainActivity", "All permissions granted.");
            } else {
                // Permissions denied
                Toast.makeText(this, "Permissions denied. The app may not function correctly.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Check if all permissions are granted
    private boolean allPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // Check if notifications are enabled for the app
    private boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    // Show dialog for notification permissions
    private void showNotificationPermissionDialog() {
        NotificationPermissionDialogFragment dialogFragment = new NotificationPermissionDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "notificationPermission");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
