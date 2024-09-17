package com.example.smishingdetectionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.SimpleCursorAdapter;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseAccess {
    private static SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
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
        private static final String KEY_ROWID = "_id";
        private static final String KEY_PHONENUMBER="Phone_Number";
        private static final String KEY_MESSAGE = "Message";
        private static final String KEY_DATE = "Date";

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
    public static boolean sendReport(int phonenumber, String message) {
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
                R.id.item_pn,
                R.id.item_date,
                R.id.item_message
        };

        return new SimpleCursorAdapter(
                context,
                R.layout.detection_item,
                cursor,
                columnsStr,
                toViewIDs
        );
    }


}
