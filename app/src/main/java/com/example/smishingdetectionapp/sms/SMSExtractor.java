package com.example.smishingdetectionapp.sms;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import com.example.smishingdetectionapp.sms.model.SMSMessage;

import java.util.ArrayList;
import java.util.List;

public class SMSExtractor {
    private final Context context;

    public SMSExtractor(Context context) {
        this.context = context;
    }

    public List<SMSMessage> extractSuspiciousMessages() {
        List<SMSMessage> suspiciousMessages = new ArrayList<>();

        // Get the SMS messages from the device
        Uri smsUri = Telephony.Sms.Inbox.CONTENT_URI;
        String[] projection = {Telephony.Sms.ADDRESS, Telephony.Sms.BODY};
        Cursor cursor = context.getContentResolver().query(smsUri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int senderIndex = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
            int bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY);

            do {
                String sender = cursor.getString(senderIndex);
                String body = cursor.getString(bodyIndex);

                // Check if the sender is not in the contacts
                if (!isSenderInContacts(sender)) {
                    // If not, create an SMSMessage object and add it to the list
                    SMSMessage smsMessage = new SMSMessage(sender, body);
                    suspiciousMessages.add(smsMessage);
                }
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            Log.e("SMSExtractor", "Failed to retrieve SMS messages.");
        }

        return suspiciousMessages;
    }

    private boolean isSenderInContacts(String sender) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(sender));
        String[] projection = {ContactsContract.PhoneLookup._ID};
        Cursor cursor = context.getContentResolver().query(lookupUri, projection, null, null, null);

        boolean isInContacts = (cursor != null && cursor.getCount() > 0);

        if (cursor != null) {
            cursor.close();
        }

        return isInContacts;
    }
}
