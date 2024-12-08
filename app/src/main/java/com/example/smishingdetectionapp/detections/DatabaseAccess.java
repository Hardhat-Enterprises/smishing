package com.example.smishingdetectionapp.detections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

import com.example.smishingdetectionapp.R;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseAccess {
    private static SQLiteOpenHelper openHelper;
    static SQLiteDatabase db;
    private static DatabaseAccess instance;
    Context context;

    public static class DatabaseOpenHelper extends SQLiteAssetHelper {

        private static final String DATABASE_NAME="detectlist.db";
        private static final int DATABASE_VERSION=1;
        private static final String TABLE_DETECTIONS = "Detections";
        private static final String TABLE_REPORTS = "Reports";
        public static final String KEY_ROWID = "_id";
        public static final String KEY_PHONENUMBER="Phone_Number";
        public static final String KEY_MESSAGE = "Message";
        public static final String KEY_DATE = "Date";

        public DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }
    }

    DatabaseAccess(Context context) {

        openHelper= new DatabaseOpenHelper(context);
        this.context = context;
    }

    public static DatabaseAccess getInstance(Context context){
        if(instance==null){
            instance=new DatabaseAccess(context);
        }
        return instance;
    }

    public void open(){
        this.db=openHelper.getWritableDatabase();
        System.out.println("Database Opened!");
    }

    public void close(){
        if(db!=null){
            this.db.close();
            System.out.println("Database Closed!");
        }
    }

    //Total detections counter
    public int getCounter() {
        Cursor cursor = db.rawQuery("select * from Detections", null);
        System.out.println("Number of Records: "+cursor.getCount());
        return cursor.getCount();
    }

    //Used to get current device time
    private static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    //Report sending function with database
    public static boolean sendReport(String phonenumber, String message) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseOpenHelper.KEY_PHONENUMBER, phonenumber);
        contentValues.put(DatabaseOpenHelper.KEY_MESSAGE, message);
        contentValues.put(DatabaseOpenHelper.KEY_DATE, getDateTime());
        long result = db.insert(DatabaseOpenHelper.TABLE_REPORTS, null, contentValues);
        return result != -1;
    }

    public SimpleCursorAdapter populateDetectionList(){

        String[] columns = {
                DatabaseOpenHelper.KEY_ROWID,
                DatabaseOpenHelper.KEY_PHONENUMBER,
                DatabaseOpenHelper.KEY_MESSAGE,
                DatabaseOpenHelper.KEY_DATE
        };

        Cursor cursor = db.query(
                DatabaseOpenHelper.TABLE_DETECTIONS,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);

        String[] columnsStr = new String[]{
                DatabaseOpenHelper.KEY_ROWID,
                DatabaseOpenHelper.KEY_PHONENUMBER,
                DatabaseOpenHelper.KEY_DATE,
                DatabaseOpenHelper.KEY_MESSAGE
        };

        int[] toViewIDs = new int[]{
                R.id.item_id,
                R.id.detectionPhoneText,
                R.id.detectionDateText,
                R.id.detectionMessageText
        };

        return new SimpleCursorAdapter(
                context,
                R.layout.detection_items,
                cursor,
                columnsStr,
                toViewIDs
        );
    }

    public void logAllReports() {
        final String TAG = "DatabaseReports"; // Define a unique tag for Logcat

        String[] columns = {
                DatabaseOpenHelper.KEY_PHONENUMBER,
                DatabaseOpenHelper.KEY_MESSAGE,
                DatabaseOpenHelper.KEY_DATE
        };

        Cursor cursor = db.query(
                DatabaseOpenHelper.TABLE_REPORTS, // Table name
                columns,                          // Columns to retrieve
                null,                             // WHERE clause
                null,                             // WHERE arguments
                null,                             // GROUP BY clause
                null,                             // HAVING clause
                null                              // ORDER BY clause
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseOpenHelper.KEY_PHONENUMBER));
                String message = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseOpenHelper.KEY_MESSAGE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseOpenHelper.KEY_DATE));

                // Log the details of each report using Log.d
                Log.d(TAG, "Phone Number: " + phoneNumber +
                        ", Message: " + message +
                        ", Date: " + date);
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "No reports found in the table.");
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    public boolean deleteReportByPhoneNumber(String phoneNumber) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        // Define the WHERE clause and arguments
        String whereClause = DatabaseOpenHelper.KEY_PHONENUMBER + "=?";
        String[] whereArgs = new String[]{phoneNumber};

        // Perform the deletion
        int rowsDeleted = db.delete(DatabaseOpenHelper.TABLE_REPORTS, whereClause, whereArgs);

        // Check if the deletion was successful
        if (rowsDeleted > 0) {
            Log.d("DatabaseAccess", "Deleted " + rowsDeleted + " report(s) for phone number: " + phoneNumber);
            return true;
        } else {
            Log.d("DatabaseAccess", "No report found for phone number: " + phoneNumber);
            return false;
        }
    }



}
