package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;
import android.widget.Toast;


public class AccessibilityActivity extends AppCompatActivity {

    private Switch colorblindSwitch;
    private TextView textView;
    private SeekBar textSizeSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accessibility);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button to go back to settings dashboard
        ImageButton report_back = findViewById(R.id.accessibility_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // Initialize Views
        textView = findViewById(R.id.enlargetext);
        textSizeSeekBar = findViewById(R.id.seekBar3);
        colorblindSwitch = findViewById(R.id.colorblindSwitch);

        // Ensure views are not null before accessing them
        if (textView != null && textSizeSeekBar != null && colorblindSwitch != null) {
            // Set SeekBar Listener for text size adjustment
            textSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // float textSize = progress * getResources().getDimension(R.dimen.text_size_multiplier);
                    // PreferenceManager.setFontSize(AccessibilityActivity.this, textSize);
                    // AppPreferences.applyFontSize(textView);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            // Set Colorblind Switch Listener
            colorblindSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    applyColorblindMode(isChecked);
                }
            });

            // Apply colorblind mode based on switch state
            applyColorblindMode(colorblindSwitch.isChecked());
        } else {
            // Handle if any of the views are null
            Toast.makeText(this, "Some views are null", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to apply colorblind mode globally
    private void applyColorblindMode(boolean colorblindModeEnabled) {
        if (colorblindModeEnabled) {
            // Apply colorblind mode to all relevant views in this activity
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorblind_text_color));
            textView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorblind_background_color));
            // Apply colorblind mode to other relevant views here
        } else {
            // Apply normal mode to all relevant views in this activity
            textView.setTextColor(ContextCompat.getColor(this, R.color.normal_text_color));
            textView.setBackgroundColor(ContextCompat.getColor(this, R.color.normal_background_color));
            // Apply normal mode to other relevant views here
        }
    }
}


