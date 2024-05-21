package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smishingdetectionapp.databinding.ActivityMainBinding;
import com.example.smishingdetectionapp.news.NewsAdapter;
import com.example.smishingdetectionapp.notifications.NotificationPermissionDialogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import static com.example.smishingdetectionapp.R.string.*;
import android.view.LayoutInflater;
import android.view.View;





public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;


    public MainActivity() {
        super();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_news, R.id.nav_settings)
                .build();

        // Check if notifications are enabled and prompt if not
        if (!areNotificationsEnabled()) {
            showNotificationPermissionDialog();
        }

        showEducationalPopup();

        BottomNavigationView nav = findViewById(R.id.bottom_navigation); //variable assignment
        nav.setSelectedItemId(R.id.nav_home); //home page selected by default
        nav.setOnItemSelectedListener(menuItem -> { //selected item listener

            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                //Empty when currently selected.
                return true;
            }
            else if(id == R.id.nav_news) {

                startActivity(new Intent(getApplicationContext(), NewsActivity.class));//Starts the News activity
                overridePendingTransition(0,0);//Removes the sliding animation
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        Button debug_btn = findViewById(R.id.debug_btn);
        debug_btn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DebugActivity.class)));

        //Opens the detections page.
        Button detections_btn = findViewById(R.id.detections_btn);
        detections_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, DetectionsActivity.class));
            finish();
        });


        //start database connection
        DatabaseAccess databaseAccess=DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        //setting counter from result
        TextView total_count;
        total_count = findViewById(R.id.total_counter);
        total_count.setText(""+databaseAccess.getCounter());
        //closing the connection
        databaseAccess.close();
        //TODO: Add functionality for new detections.

    }

    private void showEducationalPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout, null);
        builder.setView(dialogView);

        // Find and set title and message
        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
        Button dialogButton = dialogView.findViewById(R.id.dialog_button);

        dialogTitle.setText(edu_popup_title);
        dialogMessage.setText(edu_popup_message);
        dialogButton.setText(edu_popup_button);

        AlertDialog dialog = builder.create();
        dialogButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    private boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

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