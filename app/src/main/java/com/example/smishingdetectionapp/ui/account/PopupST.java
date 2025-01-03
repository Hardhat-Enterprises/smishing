package com.example.smishingdetectionapp.ui.account;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smishingdetectionapp.R;

public class PopupST extends AppCompatActivity {

    private Switch sessionTimeoutSwitch;

    // SharedPreferences to store and retrieve the switch state
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "sessionTimeoutPrefs";
    private static final String SWITCH_STATE_KEY = "sessionTimeoutSwitchState";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_session_timeout_activity);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        sessionTimeoutSwitch = findViewById(R.id.sessionTimeoutSwitch);

        // Sets the default state of the switch as false
        boolean isSwitchChecked = sharedPreferences.getBoolean(SWITCH_STATE_KEY, false);
        sessionTimeoutSwitch.setChecked(isSwitchChecked);

        ImageButton backButton = findViewById(R.id.popup_session_timeout_back);
        backButton.setOnClickListener(v -> finish());

        sessionTimeoutSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Saves the switch state to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SWITCH_STATE_KEY, isChecked);
            editor.apply();
        });
    }
}