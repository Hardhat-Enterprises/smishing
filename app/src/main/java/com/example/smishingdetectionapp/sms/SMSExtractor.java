package com.example.smishingdetectionapp.sms;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.smishingdetectionapp.SmishingDetector;
import com.example.smishingdetectionapp.sms.model.SMSMessage;
import com.example.smishingdetectionapp.sms.OnStatsUpdateListener;

import java.util.ArrayList;
import java.util.List;

public class SMSExtractor {
    private final Context context;
    private int totalMessagesAnalyzed = 0;
    private int smishingMessagesCount = 0;
    private OnStatsUpdateListener statsUpdateListener;

    public SMSExtractor(Context context) {
        this.context = context;
    }

    public int getTotalMessagesAnalyzed() {
        return totalMessagesAnalyzed;
    }

    public int getSmishingMessagesCount() {
        return smishingMessagesCount;
    }

    private boolean hasSmsPermission() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasContactsPersmission() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    public void setOnStatsUpdateListener(OnStatsUpdateListener listener) {
        this.statsUpdateListener = listener;
    }

    private void notifyStatsUpdate() {
        if (statsUpdateListener != null) {
            statsUpdateListener.onStatsUpdated(totalMessagesAnalyzed, smishingMessagesCount);
        }
    }

    @Nullable
    private Cursor getSmsCursor() {

        if (!hasSmsPermission()) {
            return null;
        }
        // Get the SMS messages from the device
        Uri smsUri = Telephony.Sms.Inbox.CONTENT_URI;
        String[] projection = {Telephony.Sms.ADDRESS, Telephony.Sms.BODY};
        return context.getContentResolver().query(smsUri, projection, null, null, null);
    }

    @Nullable
    private Cursor getContactsCursor(String sender){
        if (!hasContactsPersmission()){
            return null;
        }
        // Get Contacts from the device
        Uri contactsUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(sender));
        String[] projection = {ContactsContract.PhoneLookup._ID};
        return context.getContentResolver().query(contactsUri, projection, null, null, null);
    }

    public List<SMSMessage> extractAllMessages(){
        List<SMSMessage> messages = new ArrayList<>();
        Cursor cursor = getSmsCursor();
        //totalMessagesAnalyzed = 0;

        if (cursor != null && cursor.moveToFirst()){
            int senderIndex = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
            int bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY);

            do {
                String sender = cursor.getString(senderIndex);
                String body = cursor.getString(bodyIndex);
                SMSMessage smsMessage = new SMSMessage(sender, body);
                messages.add(smsMessage);
                totalMessagesAnalyzed++;

            }while (cursor.moveToNext());
        }else {
            Log.e("SMSExtractor", "Failed to retrieve SMS messages.");
        }
        notifyStatsUpdate();
        return messages;
    }

    public List<SMSMessage> extractSuspiciousMessages() {
        List<SMSMessage> suspiciousMessages = new ArrayList<>();
        totalMessagesAnalyzed = 0;
        smishingMessagesCount = 0;
        Cursor cursor = getSmsCursor();

        if (cursor != null && cursor.moveToFirst()) {
            int senderIndex = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
            int bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY);

            do {
                String sender = cursor.getString(senderIndex);
                String body = cursor.getString(bodyIndex);
                totalMessagesAnalyzed++;

                // Check if the sender is not in the contacts
                if (!isSenderInContacts(sender)) {
                    // If not, create an SMSMessage object and add it to the list
                    boolean isSmishing = SmishingDetector.isSmishingMessage(body.toLowerCase());

                    if (isSmishing) {
                        SMSMessage smsMessage = new SMSMessage(sender, body);
                        suspiciousMessages.add(smsMessage);
                        smishingMessagesCount++;
                    }
                }
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            Log.e("SMSExtractor", "Failed to retrieve SMS messages.");
        }
        notifyStatsUpdate();
        return suspiciousMessages;
    }

    private boolean isSenderInContacts(String sender) {

        Cursor cursor = getContactsCursor(sender);
        boolean isInContacts = (cursor != null && cursor.getCount() > 0);

        if (cursor != null) {
            cursor.close();
        }
        return isInContacts;
    }

}
