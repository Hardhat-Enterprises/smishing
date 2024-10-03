package com.example.smishingdetectionapp.detections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.smishingdetectionapp.R;

import com.example.smishingdetectionapp.forum.model.ForumTopic;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        public static final String KEY_TYPE = "Type";

        // Forum table columns and names
        private static final String TABLE_FORUM_TOPICS = "ForumTopics";
        private static final String KEY_FORUM_TOPIC_ID = "_id";
        private static final String KEY_FORUM_TOPIC_TITLE = "Title";
        private static final String KEY_FORUM_TOPIC_DESCRIPTION = "Description";
        private static final String TABLE_FORUM_POSTS = "ForumPosts";
        private static final String KEY_FORUM_POST_ID = "_id";
        private static final String KEY_FORUM_POST_TITLE = "Title";
        private static final String KEY_FORUM_POST_CONTENT = "Content";

        // SQL Statement to Create the Table
        private static final String TABLE_FORUM_TOPICS_CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_FORUM_TOPICS + " (" +
                        KEY_FORUM_TOPIC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        KEY_FORUM_TOPIC_TITLE + " TEXT NOT NULL, " +
                        KEY_FORUM_TOPIC_DESCRIPTION + " TEXT NOT NULL);";

        public DatabaseOpenHelper(Context context) {
            super();

        }
    }

    DatabaseAccess(Context context) {

        openHelper= new MySQLiteOpenHelper(context);
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
        db.execSQL(DatabaseOpenHelper.TABLE_FORUM_TOPICS_CREATE);
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

    public int SmishingCounter(){
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Detections WHERE Type = 'Smishing'", null);
        int count = 0;
        if (cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int HamCounter(){
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Detections WHERE Type = 'Ham'", null);
        int count = 0;
        if (cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int SpamCounter(){
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Detections WHERE Type = 'Spam'", null);
        int count = 0;
        if (cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
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

    public Cursor populateList(){
        SQLiteDatabase db = openHelper.getReadableDatabase();

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

    private static class MySQLiteOpenHelper extends SQLiteOpenHelper {
        public MySQLiteOpenHelper(Context context) {
            super(context);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }}


    // Simulate submission of comments
    public static boolean submitComment(String comment) {
        // Add your database logic or API call here
        return !comment.isEmpty(); // Simulating success
    }

    public static boolean submitForumTopic(String title, String description) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseOpenHelper.KEY_FORUM_TOPIC_TITLE, title);
        contentValues.put(DatabaseOpenHelper.KEY_FORUM_TOPIC_DESCRIPTION, description);
        long result = db.insert(DatabaseOpenHelper.TABLE_FORUM_TOPICS, null, contentValues);
        return result != -1;
    }

    // Method to fetch a list of forum topics
    public List<ForumTopic> getForumTopics() {
        List<ForumTopic> forumTopics = new ArrayList<>();

        // Open database for reading
        SQLiteDatabase db = openHelper.getReadableDatabase();

        // Define the columns to retrieve
        String[] columns = {
                DatabaseOpenHelper.KEY_FORUM_TOPIC_TITLE,
                DatabaseOpenHelper.KEY_FORUM_TOPIC_DESCRIPTION
        };

        // Query the table
        Cursor cursor = db.query(
                DatabaseOpenHelper.TABLE_FORUM_TOPICS, // Table name
                columns, // Columns to return
                null,    // Selection (WHERE clause)
                null,    // Selection arguments
                null,    // Group by
                null,    // Having
                null     // Order by
        );

        // Iterate through the results and add to the list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseOpenHelper.KEY_FORUM_TOPIC_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseOpenHelper.KEY_FORUM_TOPIC_DESCRIPTION));
                forumTopics.add(new ForumTopic(title, description));
            } while (cursor.moveToNext());

            // Close the cursor when done
            cursor.close();
        }

        return forumTopics;
        String query = "SELECT _id, Phone_Number, Message, Date, Type FROM Detections";

        return db.rawQuery(query, null);
    }
}
