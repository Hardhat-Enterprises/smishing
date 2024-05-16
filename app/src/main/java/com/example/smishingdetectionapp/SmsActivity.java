package com.example.smishingdetectionapp;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smishingdetectionapp.data.model.SMSMessage;

import java.util.ArrayList;

public class SmsActivity extends AppCompatActivity {
    private ArrayList<SMSMessage> smsMessageList = new ArrayList<>();
    private static final int READ_SMS_PERMISSION_CODE = 1;

    private TextView messagesText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        ImageButton back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(v -> {
            finish();
        });

        messagesText = findViewById(R.id.message_text);

        //checking the sms read permission on runtime
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            //requesting permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_SMS}, READ_SMS_PERMISSION_CODE);
        } else {
            getInboxMessages();
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

                messagesText.append(smsMessage.getSender());
                messagesText.append("\n");
                messagesText.append(smsMessage.getBody());
                messagesText.append("\n\n");
                Log.e("SMISHING", smsMessage.getBody());
            } while (cursor.moveToNext());
        } else {
            messagesText.setText(getString(R.string.empty_sms));
        }

        if (cursor != null) {
            cursor.close();
        }
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
}