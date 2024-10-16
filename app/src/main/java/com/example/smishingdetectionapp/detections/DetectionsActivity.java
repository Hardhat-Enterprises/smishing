package com.example.smishingdetectionapp.detections;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetectionsActivity extends AppCompatActivity {

    private ListView detectionLV;
    DatabaseAccess databaseAccess;
    private DisplayDataAdapterView adapter;

    //adapter used to search standard queries.
    public void dataAdapter(String searchQuery){
        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //adapter used to search the database using a parameterised query to prevent SQL injection.
    public void dataSearchAdapter(String searchQuery, String[] selectionArgs){
        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, selectionArgs);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //Securely searching the database.
    public void searchDB(String search){
        String searchQuery = ("SELECT * FROM Detections WHERE " +
                "Phone_Number LIKE ?" +
                "OR Message LIKE ?" +
                "OR Date LIKE ?" +
                "OR Type LIKE ?");

        String dataSearch = "%" + search + "%";
        String[] selectionArgs = new String[]{dataSearch, dataSearch, dataSearch, dataSearch};
        dataSearchAdapter(searchQuery, selectionArgs);
    }

    //Sorting Oldest Date to Newest Date
    public void sortONDB(){
        String searchQuery = ("SELECT * FROM Detections ORDER BY Date ASC");
        dataAdapter(searchQuery);
    }

    //Sorting Newest Date to Oldest Date
    public void sortNODB(){
        String searchQuery = ("SELECT * FROM Detections ORDER BY Date DESC");
        dataAdapter(searchQuery);
    }

    //Sorting by Smishing detections first
    public void sortSMISHING(){
        String searchQuery = ("SELECT * FROM Detections ORDER BY CASE WHEN Type = 'Smishing' THEN 1 WHEN Type = 'Spam' THEN 2 WHEN Type = 'Ham' THEN 3 END");
        dataAdapter(searchQuery);
    }

    //Sorting by Spam detections first
    public void sortSpam(){
        String searchQuery = ("SELECT * FROM Detections ORDER BY CASE WHEN Type = 'Spam' THEN 1 WHEN Type = 'Ham' THEN 2 WHEN Type = 'Smishing' THEN 3 END");
        dataAdapter(searchQuery);
    }

    //Sorting by Ham detections first
    public void sortHam(){
        String searchQuery = ("SELECT * FROM Detections ORDER BY CASE WHEN Type = 'Ham' THEN 1 WHEN Type = 'Spam' THEN 2 WHEN Type = 'Smishing' THEN 3 END");
        dataAdapter(searchQuery);
    }

    //Used for updating the list when an item is deleted.
    public void refreshList(){
        String searchQuery = ("SELECT * FROM Detections");
        dataAdapter(searchQuery);
    }

    //Deleting a row (detection)
    public void DeleteRow(String id) {
        DatabaseAccess.db.delete("Detections", "_id" + "=" + id, null);
    }

    //Saving checked state of radio buttons in the filter popup.
    private void saveRadioButtonState(String key, boolean isChecked) {
        SharedPreferences sharedPreferences = getSharedPreferences("RadioPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
    }

    //function used to clear checked state of radio buttons
    private void clearRadioButtonState() {
        SharedPreferences sharedPreferences = getSharedPreferences("RadioPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    //clears checked state of radio buttons when app is closed.
    @Override
    protected void onStop() {
        super.onStop();
        clearRadioButtonState();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detections);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Back button to go back to main dashboard
        ImageButton detections_back = findViewById(R.id.detections_back);
        detections_back.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            clearRadioButtonState(); //clears checked state of radio buttons when returning to app dashboard.
        });

        //Defining and populating listview from database.
        detectionLV = findViewById(R.id.lvDetectionsList);
        databaseAccess = new DatabaseAccess(getApplicationContext());
        databaseAccess.open();
        Cursor cursor = databaseAccess.populateList();
        adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);

        EditText detSearch = findViewById(R.id.searchTextBox);
        detSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search = s.toString();
                searchDB(search);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("RadioPrefs", MODE_PRIVATE);
        ImageView filterBtn = findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(v -> {
            //determining which activity to show when filter button is clicked.
            View bottomSheet = getLayoutInflater().inflate(R.layout.popup_filter, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetectionsActivity.this);
            bottomSheetDialog.setContentView(bottomSheet);
            bottomSheetDialog.show();

            //defining radio buttons in the PopupFilter window.
            RadioButton OldToNewRB = bottomSheet.findViewById(R.id.OldToNewRB);
            RadioButton NewToOldRB = bottomSheet.findViewById(R.id.NewToOldRB);
            RadioButton radioSmishing = bottomSheet.findViewById(R.id.radioSmishing);
            RadioButton radioSpam = bottomSheet.findViewById(R.id.radioSpam);
            RadioButton radioHam = bottomSheet.findViewById(R.id.radioHam);

            //default value of radio buttons before saving preference.
            OldToNewRB.setChecked(sharedPreferences.getBoolean("OldToNewRB", false));
            NewToOldRB.setChecked(sharedPreferences.getBoolean("NewToOldRB", false));
            radioSmishing.setChecked(sharedPreferences.getBoolean("radioSmishing", false));
            radioSpam.setChecked(sharedPreferences.getBoolean("radioSpam", false));
            radioHam.setChecked(sharedPreferences.getBoolean("radioHam", false));

            OldToNewRB.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(OldToNewRB.isChecked()) {
                    NewToOldRB.setChecked(false);//unchecks other radio button when this one is checked.
                    radioSpam.setChecked(false);
                    radioHam.setChecked(false);
                    radioSmishing.setChecked(false);
                    sortONDB();//uses sort function
                }
                saveRadioButtonState("OldToNewRB", isChecked); //saves the radio button checked state.
            });
            NewToOldRB.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(NewToOldRB.isChecked()) {
                    OldToNewRB.setChecked(false);
                    radioSpam.setChecked(false);
                    radioHam.setChecked(false);
                    radioSmishing.setChecked(false);
                    sortNODB();
                }
                saveRadioButtonState("NewToOldRB", isChecked);
            });
            radioSmishing.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(radioSmishing.isChecked()) {
                    radioSpam.setChecked(false);
                    radioHam.setChecked(false);
                    NewToOldRB.setChecked(false);
                    OldToNewRB.setChecked(false);
                    sortSMISHING();
                }
                saveRadioButtonState("radioSmishing", isChecked);
            });
            radioSpam.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(radioSpam.isChecked()) {
                    radioSmishing.setChecked(false);
                    radioHam.setChecked(false);
                    NewToOldRB.setChecked(false);
                    OldToNewRB.setChecked(false);
                    sortSpam();
                }
                saveRadioButtonState("radioSpam", isChecked);
            });
            radioHam.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(radioHam.isChecked()) {
                    radioSpam.setChecked(false);
                    radioSmishing.setChecked(false);
                    NewToOldRB.setChecked(false);
                    OldToNewRB.setChecked(false);
                    sortHam();
                }
                saveRadioButtonState("radioHam", isChecked);
            });

        });

        detectionLV.setOnItemLongClickListener((parent, view, position, id) -> {
            View bottomSheetDel = getLayoutInflater().inflate(R.layout.popup_deleteitem, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetectionsActivity.this);
            bottomSheetDialog.setContentView(bottomSheetDel);
            bottomSheetDialog.show();

            Button Cancel = bottomSheetDel.findViewById(R.id.delItemCancel);
            Button Confirm = bottomSheetDel.findViewById(R.id.DelItemConfirm);

            Cancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

            Confirm.setOnClickListener(v -> {
                DeleteRow(String.valueOf(id));
                refreshList();
                bottomSheetDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Detection Deleted!", Toast.LENGTH_SHORT).show();
            });
            return true;
        });

    }
}