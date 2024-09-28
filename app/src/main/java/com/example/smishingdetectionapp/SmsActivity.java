

package com.example.smishingdetectionapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.sms.SMSAdapter;
import com.example.smishingdetectionapp.sms.SMSClickListener;
import com.example.smishingdetectionapp.sms.model.SMSMessage;

import java.util.ArrayList;
import java.util.List;

public class SmsActivity extends AppCompatActivity implements SMSClickListener {
    private static final String TAG = "SmsActivity";
    private static final String CHANNEL_ID = "smishing_alert_channel";
    private static final int READ_SMS_PERMISSION_CODE = 1;
    private static final int NOTIFICATION_ID = 1001;
    private static final int PERMISSION_REQUEST_CODE = 123;

    private ArrayList<SMSMessage> smsMessageList = new ArrayList<>();
    private RecyclerView smsRecyclerView;
    private SMSAdapter smsAdapter;
    private TextView noSMSMessagesText;

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                SmsMessage[] messages = extractSmsMessages(intent);
                if (messages != null) {
                    for (SmsMessage sms : messages) {
                        if (sms != null) {
                            String sender = sms.getDisplayOriginatingAddress();
                            String messageBody = sms.getMessageBody();

                            if (sender != null && messageBody != null) {
                                SMSMessage smsMessage = new SMSMessage(sender, messageBody);
                                smsMessageList.add(smsMessage);
                                smsAdapter.updateMessagesList(smsMessageList);

                                // Check if message is suspicious and show notification
                                if (SmishingDetector.isSmishingMessage(messageBody.toLowerCase())) {
                                    showNotification(context, sender, messageBody);
                                }
                            } else {
                                Log.e(TAG, "Sender or message body is null");
                            }
                        }
                    }
                }
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        noSMSMessagesText = findViewById(R.id.no_messages_text);
        smsRecyclerView = findViewById(R.id.messages_recycler_view);
        smsRecyclerView.setHasFixedSize(true);
        smsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        smsAdapter = new SMSAdapter(this);
        smsRecyclerView.setAdapter(smsAdapter);

        // Check if activity was started from notification
        Intent intent = getIntent();
        String sender = intent.getStringExtra("SMS_SENDER");
        String messageBody = intent.getStringExtra("SMS_BODY");

        if (sender != null && messageBody != null) {
            // If sender and message body are present, open the details activity directly
            openSMSMessageDetailActivity(sender, messageBody);
        } else {
            // Otherwise, handle the regular case of showing all messages
            // Register receiver for incoming SMS
            registerReceiver(smsReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));

            // Check SMS permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, READ_SMS_PERMISSION_CODE);
            } else {
                // Extract and display all messages
                getInboxMessages();
            }
        }
    }

    // New helper method to open SMS details
    private void openSMSMessageDetailActivity(String sender, String messageBody) {
        Intent detailIntent = new Intent(SmsActivity.this, SMSMessageDetailActivity.class);
        detailIntent.putExtra("SMS_SENDER", sender);
        detailIntent.putExtra("SMS_BODY", messageBody);
        startActivity(detailIntent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    private SmsMessage[] extractSmsMessages(Intent intent) {
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        if (pdus == null) {
            Log.e(TAG, "No PDUs found in the intent");
            return null;
        }

        SmsMessage[] messages = new SmsMessage[pdus.length];
        String format = intent.getStringExtra("format");
        for (int i = 0; i < pdus.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
        }

        return messages;
    }

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
                SMSMessage smsMessage = new SMSMessage(sender, body);
                smsMessageList.add(smsMessage);
            } while (cursor.moveToNext());
            smsAdapter.updateMessagesList(smsMessageList);
            noSMSMessagesText.setVisibility(View.GONE);
        } else {
            noSMSMessagesText.setVisibility(View.VISIBLE);
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    private void showNotification(Context context, String sender, String messageBody) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Smishing Alerts", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, SmsActivity.class);
        intent.putExtra("SMS_SENDER", sender);
        intent.putExtra("SMS_BODY", messageBody);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.hardhat_logo)
                .setContentTitle("Suspicious Message Detected")
                .setContentText("Possible phishing message from " + sender)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        }
    }


public void OnMessageClicked(com.example.smishingdetectionapp.sms.model.SMSMessage message) {
    Intent intent = new Intent(SmsActivity.this, SMSMessageDetailActivity.class);
    intent.putExtra("SMS_MESSAGE", message);
    startActivity(intent);
}
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
}

