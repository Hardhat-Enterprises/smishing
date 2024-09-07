package com.example.smishingdetectionapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.smishingdetectionapp.sms.SMSAdapter;
import com.example.smishingdetectionapp.sms.SMSClickListener;
import com.example.smishingdetectionapp.sms.model.SMSMessage;
import com.example.smishingdetectionapp.sms.SMSExtractor;


import java.util.ArrayList;
import java.util.List;

public class SmsActivity extends AppCompatActivity implements SMSClickListener {
    private ArrayList<SMSMessage> smsMessageList = new ArrayList<>();
    private static final int READ_SMS_PERMISSION_CODE = 1;
    private static final int NOTIFICATION_ID = 1001;
    private static final int PERMISSION_REQUEST_CODE = 123;

    RecyclerView smsRecyclerView; // Holds the recyclerview which displays the sms messages list
    SMSAdapter smsAdapter;
    TextView noSMSMessagesText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        noSMSMessagesText = findViewById(R.id.no_messages_text);
        smsRecyclerView = findViewById(R.id.messages_recycler_view);
        smsRecyclerView.setHasFixedSize(true);
        smsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        smsAdapter = new SMSAdapter(this);
        smsRecyclerView.setAdapter(smsAdapter);

        SMSExtractor smsExtractor = new SMSExtractor(getApplicationContext());

        // Check SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, READ_SMS_PERMISSION_CODE);
        } else {
            // Extract and display all messages
            List<SMSMessage> allMessages = smsExtractor.extractAllMessages();
            smsAdapter.updateMessagesList(allMessages);

            // Check for suspicious messages and show notifications
            List<SMSMessage> suspiciousMessages = smsExtractor.extractSuspiciousMessages();
            for (SMSMessage smsMessage : suspiciousMessages) {
                // to do implement a function to display notification
                showNotification(smsMessage);
            }
        }
    }

    /**
     * get all the inbox messages
     */
    private void getInboxMessages() {
        ContentResolver contentResolver = getContentResolver();
        Uri inboxUri = Uri.parse("content://sms/inbox");
        Cursor cursor = contentResolver.query(
                inboxUri,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                SMSMessage smsMessage = new SMSMessage(sender, body);//create Message object
                smsMessageList.add(smsMessage); // add to the list
                smsAdapter.updateMessagesList(smsMessageList);
            } while (cursor.moveToNext());
            noSMSMessagesText.setVisibility(View.GONE);
        } else {
            noSMSMessagesText.setVisibility(View.VISIBLE);
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public void OnMessageClicked(com.example.smishingdetectionapp.sms.model.SMSMessage message) {
        Intent intent = new Intent(SmsActivity.this, SMSMessageDetailActivity.class);
        intent.putExtra("SMS_MESSAGE", message);
        startActivity(intent);
    }

    /**
     * permission receiver
     * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getInboxMessages();
            }
        }
    }
    private void showNotification(SMSMessage smsMessage) {
        String channelId = "smishing_alert_channel";
        String channelName = "Smishing Alerts";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, SMSMessageDetailActivity.class);
        intent.putExtra("SMS_MESSAGE", smsMessage);
        // Use FLAG_IMMUTABLE for PendingIntent if you do not need it to be mutable
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.hardhat_logo) // Replace with your app's icon
                .setContentTitle("Suspicious Message Detected")
                .setContentText("Possible phishing message from " + smsMessage.getSender())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent) // Opens detail activity on click
                .setAutoCancel(true); // Dismisses notification on click

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            return;
        }
        notificationManager.notify((int) System.currentTimeMillis(), builder.build()); // Use unique ID
    }

}