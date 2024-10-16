package com.example.smishingdetectionapp.detections;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.smishingdetectionapp.R;

//Used for populating listview with correct data while searching.
public class DisplayDataAdapterView extends CursorAdapter {

    public DisplayDataAdapterView(DetectionsActivity context, Cursor c) {
        super(context, c, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.detection_items, parent,
                false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView PhoneTextView = view.findViewById(R.id.detectionPhoneText);
        TextView MessageTextView = view.findViewById(R.id.detectionMessageText);
        TextView DateTextView = view.findViewById(R.id.detectionDateText);
        TextView TypeTextView = view.findViewById(R.id.detectionType);

        int Phone_Number =
                cursor.getColumnIndex(DatabaseAccess.DatabaseOpenHelper.KEY_PHONENUMBER);
        int Message =
                cursor.getColumnIndex(DatabaseAccess.DatabaseOpenHelper.KEY_MESSAGE);
        int Date =
                cursor.getColumnIndex(DatabaseAccess.DatabaseOpenHelper.KEY_DATE);
        int Type =
                cursor.getColumnIndex(DatabaseAccess.DatabaseOpenHelper.KEY_TYPE);

        String Phone = cursor.getString(Phone_Number);
        String Messages = cursor.getString(Message);
        String Dates = cursor.getString(Date);
        String Types = cursor.getString(Type);

        PhoneTextView.setText(Phone);
        MessageTextView.setText(Messages);
        DateTextView.setText(Dates);
        TypeTextView.setText(Types);

        String detectionType = cursor.getString(cursor.getColumnIndexOrThrow("Type"));
        if (detectionType.equalsIgnoreCase("Smishing")) {
            TypeTextView.setTextColor(Color.RED);
        } else if (detectionType.equalsIgnoreCase("Spam")) {
            TypeTextView.setTextColor(Color.parseColor("#FFA726"));
        } else if (detectionType.equalsIgnoreCase("Ham")) {
            TypeTextView.setTextColor(Color.parseColor("#008a22"));
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

}
