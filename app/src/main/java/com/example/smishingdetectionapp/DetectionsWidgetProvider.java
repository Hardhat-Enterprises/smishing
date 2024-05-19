package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

public class DetectionsWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            // Get the layout for the App Widget and attach an on-click listener
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_detections);

            // Set up the intent that starts the DetectionsActivity
            Intent intent = new Intent(context, DetectionsActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

            // Update the widget list view with the detection data
            setListData(context, views);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void setListData(Context context, RemoteViews views) {
        // Fetch data from the database and populate the widget list view
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        Cursor cursor = databaseAccess.getRecentDetections();
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    // Get the detection message
                    String detection = cursor.getString(cursor.getColumnIndex(DatabaseAccess.DatabaseOpenHelper.KEY_MESSAGE));
                    // Update the widget list view with the detection data
                    views.setTextViewText(R.id.widget_list, detection);
                }
            } finally {
                cursor.close();
            }
        }
        databaseAccess.close();
    }
}
