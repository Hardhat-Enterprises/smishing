package com.example.smishingdetectionapp;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
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

import com.example.smishingdetectionapp.data.model.Message;

import java.util.ArrayList;

public class SmsActivity extends AppCompatActivity {
    private ArrayList<Message> messageList = new ArrayList<>();
    private static final int READ_SMS_PERMISSION_CODE = 1;

    private TextView messagesText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

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
                Message message = new Message(sender, body);//create Message object
                messageList.add(message); // add to the list

                messagesText.append(message.getSender());
                messagesText.append("\n");
                messagesText.append(message.getBody());
                messagesText.append("\n\n");
                Log.e("SMISHING", message.getBody());
            } while (cursor.moveToNext());
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