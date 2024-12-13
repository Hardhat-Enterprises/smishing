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
    public static final String TABLE_USERS = "Users";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_VERIFICATION_CODE = "verification_code";

    public Cursor rawQuery(String query, String[] selectionArgs) {
        if (db == null || !db.isOpen()) {
            Log.e("DatabaseAccess", "Database is not initialized or is closed.");
            return null;
        }

        try {
            // Execute the raw query and return the Cursor
            return db.rawQuery(query, selectionArgs);
        } catch (Exception e) {
            Log.e("DatabaseAccess", "Error executing rawQuery: " + e.getMessage(), e);
            return null;
        }
    }

    public boolean delete(String tableName, String whereClause, String[] whereArgs) {
        if (db == null || !db.isOpen()) {
            Log.e("DatabaseAccess", "Database is not initialized or is closed.");
            return false;
        }

        try {
            int rowsDeleted = db.delete(tableName, whereClause, whereArgs);
            if (rowsDeleted > 0) {
                Log.d("DatabaseAccess", "Successfully deleted row(s) from table: " + tableName);
                return true;
            } else {
                Log.e("DatabaseAccess", "No rows were deleted from table: " + tableName);
                return false;
            }
        } catch (Exception e) {
            Log.e("DatabaseAccess", "Error deleting row(s): " + e.getMessage(), e);
            return false;
        }
    }

    public boolean sendReport(int phoneNumber, String message) {
        if (db == null || !db.isOpen()) {
            Log.e("DatabaseAccess", "Database is not initialized or is closed.");
            return false;
        }

        try {
            // Prepare the values to be inserted
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseOpenHelper.KEY_PHONENUMBER, phoneNumber);
            contentValues.put(DatabaseOpenHelper.KEY_MESSAGE, message);
            contentValues.put(DatabaseOpenHelper.KEY_DATE, getDateTime()); // Add current date and time

            // Insert the report into the database
            long result = db.insert(DatabaseOpenHelper.TABLE_REPORTS, null, contentValues);

            if (result != -1) {
                Log.d("DatabaseAccess", "Report inserted successfully! Phone Number: " + phoneNumber + ", Message: " + message);
                return true;
            } else {
                Log.e("DatabaseAccess", "Failed to insert the report.");
                return false;
            }
        } catch (Exception e) {
            Log.e("DatabaseAccess", "Exception in sendReport: " + e.getMessage(), e);
            return false;
        }
    }

    private static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(new Date());
    }



    // Database Helper
    public static class DatabaseOpenHelper extends SQLiteAssetHelper {
        public static final String DATABASE_NAME = "detectlist.db";
        public static final int DATABASE_VERSION = 3;
        public static final String KEY_ROWID = "row_id" ;
        public static final String KEY_PHONENUMBER = "phone_number";
        public static final String KEY_MESSAGE = "message" ;
        public static final String KEY_DATE = "date";
        public static final String TABLE_REPORTS = "Reports";


        public DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d("DatabasePath", "Database path: " + context.getDatabasePath(DATABASE_NAME));
        }

    }

    // Singleton instance
    private DatabaseAccess(Context context) {
        openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        if (db == null || !db.isOpen()) {
            db = openHelper.getWritableDatabase();
            Log.d("DatabaseAccess", "Database Opened!");
        }
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
            Log.d("DatabaseAccess", "Database Closed!");
        }
    }

    // Save verification code
    public boolean saveVerificationCode(String email, String code) {
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_EMAIL, email);
            values.put(KEY_VERIFICATION_CODE, code);

            // Check if email exists in the database
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_EMAIL + " = ?", new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                // Update the record if it exists
                int rowsUpdated = db.update(TABLE_USERS, values, KEY_EMAIL + " = ?", new String[]{email});
                cursor.close();
                Log.d("DatabaseAccess", "Saving Code: " + code + " for Email: " + email);
                Log.d("DatabaseAccess", "Rows updated: " + rowsUpdated);
                return rowsUpdated > 0;
            } else {
                // Insert a new record if it doesn't exist
                values.put(KEY_PASSWORD, ""); // Default empty password
                long result = db.replace(TABLE_USERS, null, values);
                Log.d("DatabaseAccess", "Saving Code: " + code + " for Email: " + email);
                Log.d("DatabaseAccess", "Insert result: " + result);
                return result != -1;
            }
        } catch (Exception e) {
            Log.e("DatabaseAccess", "Error in saveVerificationCode: " + e.getMessage());
            return false;
        }
    }

    public boolean checkVerificationCode(String email, String enteredCode) {
        try {
            String query = "SELECT " + KEY_VERIFICATION_CODE + " FROM " + TABLE_USERS + " WHERE " + KEY_EMAIL + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                // Fetch the stored verification code
                String dbCode = cursor.getString(cursor.getColumnIndexOrThrow(KEY_VERIFICATION_CODE));
                Log.d("DatabaseAccess", "Fetched Code: " + dbCode + " | Entered Code: " + enteredCode);
                cursor.close();

                // Trim and compare codes case-insensitively
                return dbCode.trim().equalsIgnoreCase(enteredCode.trim());
            } else {
                Log.d("DatabaseAccess", "No matching email found: " + email);
                return false;
            }
        } catch (Exception e) {
            Log.e("DatabaseAccess", "Error in checkVerificationCode: " + e.getMessage(), e);
            return false;
        }
    }

    // Update user password
    public boolean updatePassword(String email, String newPassword) {
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_PASSWORD, newPassword);
            int rowsUpdated = db.update(TABLE_USERS, values, KEY_EMAIL + " = ?", new String[]{email});
            return rowsUpdated > 0;
        } catch (Exception e) {
            Log.e("DatabaseAccess", "Error in updatePassword: " + e.getMessage());
            return false;
        }
    }
}
