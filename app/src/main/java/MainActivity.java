import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS_CONTACTS = 1;
    private TextView smsTextView;

    @SuppressLint("MissingInflatedId")
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


