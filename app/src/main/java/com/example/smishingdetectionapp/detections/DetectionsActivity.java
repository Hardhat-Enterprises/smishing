package com.example.smishingdetectionapp.detections;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DetectionsActivity extends AppCompatActivity {

    private ListView detectionLV;
    private DatabaseAccess databaseAccess;

    // Search the database with a query
    public void searchDB(String search) {
        databaseAccess.open();
        String searchQuery = "SELECT * FROM Detections WHERE Phone_Number LIKE ? OR Message LIKE ? OR Date LIKE ?";
        Cursor cursor = databaseAccess.rawQuery(searchQuery, new String[]{
                "%" + search + "%", "%" + search + "%", "%" + search + "%"
        });

        if (cursor != null) {
            DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
            detectionLV.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Log.e("DetectionsActivity", "Cursor is null. Query failed.");
        }
        databaseAccess.close();
    }

    // Sort database records by oldest date
    public void sortONDB() {
        databaseAccess.open();
        String searchQuery = "SELECT * FROM Detections ORDER BY Date ASC";
        Cursor cursor = databaseAccess.rawQuery(searchQuery, null);

        if (cursor != null) {
            DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
            detectionLV.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Log.e("DetectionsActivity", "Cursor is null. Query failed.");
        }
        databaseAccess.close();
    }

    // Sort database records by newest date
    public void sortNODB() {
        databaseAccess.open();
        String searchQuery = "SELECT * FROM Detections ORDER BY Date DESC";
        Cursor cursor = databaseAccess.rawQuery(searchQuery, null);

        if (cursor != null) {
            DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
            detectionLV.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Log.e("DetectionsActivity", "Cursor is null. Query failed.");
        }
        databaseAccess.close();
    }

    // Refresh the detection list
    public void refreshList() {
        databaseAccess.open();
        String searchQuery = "SELECT * FROM Detections";
        Cursor cursor = databaseAccess.rawQuery(searchQuery, null);

        if (cursor != null) {
            DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
            detectionLV.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Log.e("DetectionsActivity", "Cursor is null. Query failed.");
        }
        databaseAccess.close();
    }

    // Delete a specific row from the database
    public void deleteRow(String id) {
        databaseAccess.open();
        boolean isDeleted = databaseAccess.delete("Detections", "_id=?", new String[]{id});

        if (isDeleted) {
            Toast.makeText(this, "Detection deleted!", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("DetectionsActivity", "Failed to delete row with id: " + id);
        }

        refreshList();
        databaseAccess.close();
    }

    // Save radio button state
    private void saveRadioButtonState(String key, boolean isChecked) {
        SharedPreferences sharedPreferences = getSharedPreferences("RadioPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
    }

    // Clear radio button state
    private void clearRadioButtonState() {
        SharedPreferences sharedPreferences = getSharedPreferences("RadioPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearRadioButtonState(); // Clear radio button states when the activity stops
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detections);

        // Back button to navigate to the main activity
        ImageButton detectionsBack = findViewById(R.id.detections_back);
        detectionsBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            clearRadioButtonState();
        });

        // Initialize database access
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        detectionLV = findViewById(R.id.lvDetectionsList);

        // Populate the detection list view
        refreshList();

        // Search functionality
        EditText detSearch = findViewById(R.id.searchTextBox);
        detSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchDB(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filter functionality
        ImageView filterBtn = findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(v -> {
            View bottomSheet = getLayoutInflater().inflate(R.layout.popup_filter, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetectionsActivity.this);
            bottomSheetDialog.setContentView(bottomSheet);
            bottomSheetDialog.show();

            RadioButton oldToNewRB = bottomSheet.findViewById(R.id.OldToNewRB);
            RadioButton newToOldRB = bottomSheet.findViewById(R.id.NewToOldRB);

            SharedPreferences sharedPreferences = getSharedPreferences("RadioPrefs", MODE_PRIVATE);
            oldToNewRB.setChecked(sharedPreferences.getBoolean("OldToNewRB", false));
            newToOldRB.setChecked(sharedPreferences.getBoolean("NewToOldRB", false));

            oldToNewRB.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    newToOldRB.setChecked(false);
                    sortONDB();
                }
                saveRadioButtonState("OldToNewRB", isChecked);
            });

            newToOldRB.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    oldToNewRB.setChecked(false);
                    sortNODB();
                }
                saveRadioButtonState("NewToOldRB", isChecked);
            });
        });

        // Delete functionality for list items
        detectionLV.setOnItemLongClickListener((parent, view, position, id) -> {
            View bottomSheetDel = getLayoutInflater().inflate(R.layout.popup_deleteitem, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetectionsActivity.this);
            bottomSheetDialog.setContentView(bottomSheetDel);
            bottomSheetDialog.show();

            Button cancel = bottomSheetDel.findViewById(R.id.delItemCancel);
            Button confirm = bottomSheetDel.findViewById(R.id.DelItemConfirm);

            cancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
            confirm.setOnClickListener(v -> {
                deleteRow(String.valueOf(id));
                bottomSheetDialog.dismiss();
            });

            return true;
        });
    }
}
