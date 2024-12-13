package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.detections.DatabaseAccess;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        TextView total_count = findViewById(R.id.total_counter);

        databaseAccess.close();
    }
}
