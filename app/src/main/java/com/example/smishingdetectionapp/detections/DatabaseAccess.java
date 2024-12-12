package com.example.smishingdetectionapp.detections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

    public static boolean sendFeedback(String name, String feedback, float rating) {
        // Here you would add your logic to send feedback to the database.
        // This could involve inserting the feedback into a SQLite database,
        // sending it to a remote server via an API call, etc.

        // For now, we will simulate a successful insertion
        // by always returning true.

        if (name.isEmpty() || feedback.isEmpty()) {
            return false; // Fail if name or feedback is empty
        }

        // Simulated success
        return true;
    }

    // Simulate submission of thoughts
    public static boolean submitThoughts(String thoughts) {
        // Add your database logic or API call here
        return !thoughts.isEmpty(); // Simulating success
    }

    // Simulate submission of comments
    public static boolean submitComment(String comment) {
        // Add your database logic or API call here
        return !comment.isEmpty(); // Simulating success
    }

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

    // Add this method to DatabaseAccess.java
    // Add this method to DatabaseAccess.java
    public ReportsAdapter populateReportsList() { // same function
        try {
            String query = "SELECT * FROM Reports ORDER BY Date DESC";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.getCount() == 0) {
                cursor.close();
                return null;
            }

            return new ReportsAdapter(context, cursor);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteReport(String phoneNumber, String message) {
        try {
            // Delete the record where Phone_Number and Message both match
            int rowsDeleted = db.delete(
                    DatabaseOpenHelper.TABLE_REPORTS,
                    "Phone_Number = ? AND Message = ?", // WHERE clause
                    new String[] { phoneNumber, message } // WHERE arguments
            );
            return rowsDeleted > 0; // Return true if at least one row was deleted
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if an exception occurred
        }
    }


    public Cursor getReports() {
        try {
            // Check if the database contains any rows
            Cursor checkCursor = db.rawQuery("SELECT COUNT(*) FROM Reports", null);
            if (checkCursor != null) {
                checkCursor.moveToFirst(); // Move to the first row
                int rowCount = checkCursor.getInt(0); // Get the count of rows
                checkCursor.close(); // Close the cursor to avoid leaks

                if (rowCount == 0) {
                    // If there are no rows, return null
                    return null;
                }
            }
            // If rows exist, return the query cursor
            return db.rawQuery("SELECT * FROM Reports ORDER BY Date DESC", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Cursor getReportsNewestFirst() { // same function
        try {
            return db.rawQuery("SELECT * FROM Reports ORDER BY Date DESC", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Cursor getReportsOldestFirst() {
        try {
            return db.rawQuery("SELECT * FROM Reports ORDER BY Date ASC", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




}
