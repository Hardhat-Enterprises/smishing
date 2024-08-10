package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS_CONTACTS = 1;
    private TextView smsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smsTextView = findViewById(R.id.smsTextView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_SMS_CONTACTS);
        } else {
            displaySmsMessages();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_SMS_CONTACTS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                displaySmsMessages();
            }
        }
    }

    private void displaySmsMessages() {
        Set<String> contactNumbers = getContactNumbers();
        List<String> messages = getSmsMessages(contactNumbers);
        StringBuilder displayText = new StringBuilder();

        for (String message : messages) {
            boolean isSmishing = SmishingDetector.isSmishingMessage(message.toLowerCase());
            displayText.append(message)
                    .append("\n")
                    .append("Smishing: ")
                    .append(isSmishing)
                    .append("\n\n");
        }

        smsTextView.setText(displayText.toString());
    }

    private Set<String> getContactNumbers() {
        Set<String> contactNumbers = new HashSet<>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactNumbers.add(number.replaceAll("[^0-9]", ""));  // Normalize phone numbers
            }
            cursor.close();
        }
        return contactNumbers;
    }

    private List<String> getSmsMessages(Set<String> contactNumbers) {
        List<String> smsList = new ArrayList<>();
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                if (!contactNumbers.contains(address.replaceAll("[^0-9]", ""))) {
                    smsList.add(body);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        return smsList;
    }
}