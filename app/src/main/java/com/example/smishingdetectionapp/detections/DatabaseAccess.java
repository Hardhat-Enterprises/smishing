package com.example.smishingdetectionapp.detections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseAccess {
    private static SQLiteAssetHelper openHelper;
    private static SQLiteDatabase db;
    private static DatabaseAccess instance;

    // Database constants
    public static final String TABLE_REPORTS = "Reports";
    public static final String TABLE_USERS = "Users";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_VERIFICATION_CODE = "verification_code";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_PHONENUMBER = "phone_number";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_DATE = "date";

    // Private constructor for singleton
    private DatabaseAccess(Context context) {
        openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    // Open the database
    public void open() {
        if (db == null || !db.isOpen()) {
            db = openHelper.getWritableDatabase();
            Log.d("DatabaseAccess", "Database Opened!");
        }
    }

    // Close the database
    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
            Log.d("DatabaseAccess", "Database Closed!");
        }
    }

    // Insert a new report into the database
    public boolean sendReport(String phoneNumber, String message) {
        if (db == null || !db.isOpen()) {
            Log.e("DatabaseAccess", "Database is not initialized or is closed.");
            return false;
        }

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_PHONENUMBER, phoneNumber);
            contentValues.put(KEY_MESSAGE, message);
            contentValues.put(KEY_DATE, getDateTime());

            long result = db.insert(TABLE_REPORTS, null, contentValues);

            if (result != -1) {
                Log.d("DatabaseAccess", "Report inserted successfully! Phone Number: " + phoneNumber + ", Message: " + message);
                return true;
            } else {
                Log.e("DatabaseAccess", "Failed to insert the report.");
                return false;
            }
        } catch (Exception e) {
            Log.e("DatabaseAccess", "Error in sendReport: " + e.getMessage(), e);
            return false;
        }
    }

    // Fetch all reports
    public Cursor getAllReports() {
        if (db == null || !db.isOpen()) {
            Log.e("DatabaseAccess", "Database is not initialized or is closed.");
            return null;
        }

        return db.rawQuery("SELECT * FROM " + TABLE_REPORTS + " ORDER BY " + KEY_DATE + " DESC", null);
    }

    // Delete a specific report by phone number and message
    public boolean deleteReport(String phoneNumber, String message) {
        try {
            int rowsDeleted = db.delete(TABLE_REPORTS, KEY_PHONENUMBER + "=? AND " + KEY_MESSAGE + "=?", new String[]{phoneNumber, message});
            return rowsDeleted > 0;
        } catch (Exception e) {
            Log.e("DatabaseAccess", "Error in deleteReport: " + e.getMessage(), e);
            return false;
        }
    }

    // Helper function to fetch current date and time
    private static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    // Save verification code for a user
    public boolean saveVerificationCode(String email, String code) {
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_EMAIL, email);
            values.put(KEY_VERIFICATION_CODE, code);

            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_EMAIL + " = ?", new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                int rowsUpdated = db.update(TABLE_USERS, values, KEY_EMAIL + " = ?", new String[]{email});
                cursor.close();
                return rowsUpdated > 0;
            } else {
                values.put(KEY_PASSWORD, "");
                long result = db.insert(TABLE_USERS, null, values);
                return result != -1;
            }
        } catch (Exception e) {
            Log.e("DatabaseAccess", "Error in saveVerificationCode: " + e.getMessage(), e);
            return false;
        }
    }

    // Check if verification code matches
    public boolean checkVerificationCode(String email, String enteredCode) {
        try {
            String query = "SELECT " + KEY_VERIFICATION_CODE + " FROM " + TABLE_USERS + " WHERE " + KEY_EMAIL + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                String dbCode = cursor.getString(cursor.getColumnIndexOrThrow(KEY_VERIFICATION_CODE));
                cursor.close();
                return dbCode.trim().equalsIgnoreCase(enteredCode.trim());
            }
        } catch (Exception e) {
            Log.e("DatabaseAccess", "Error in checkVerificationCode: " + e.getMessage(), e);
        }
        return false;
    }

    // Update user password
    public boolean updatePassword(String email, String newPassword) {
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_PASSWORD, newPassword);
            int rowsUpdated = db.update(TABLE_USERS, values, KEY_EMAIL + " = ?", new String[]{email});
            return rowsUpdated > 0;
        } catch (Exception e) {
            Log.e("DatabaseAccess", "Error in updatePassword: " + e.getMessage(), e);
            return false;
        }
    }

    // DatabaseOpenHelper class
    public static class DatabaseOpenHelper extends SQLiteAssetHelper {
        private static final String DATABASE_NAME = "detectlist.db";
        private static final int DATABASE_VERSION = 3;

        public DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d("DatabasePath", "Database path: " + context.getDatabasePath(DATABASE_NAME));
        }
    }
}

