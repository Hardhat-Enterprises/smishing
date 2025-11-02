package com.example.smishingdetectionapp.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.R;

public class SupportFeedbackActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 280;

    private RadioGroup rgSolved;
    private RatingBar ratingBar;
    private EditText editFeedback;
    private TextView tvCounter, tvRatingLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_feedback);

        // ---- Views ----
        rgSolved       = findViewById(R.id.rgSolved);
        ratingBar      = findViewById(R.id.ratingBar);
        editFeedback   = findViewById(R.id.editFeedback);
        tvCounter      = findViewById(R.id.tvCounter);
        tvRatingLabel  = findViewById(R.id.tvRatingLabel);
        Button btnSubmit     = findViewById(R.id.btnSubmit);
        ImageButton btnBack  = findViewById(R.id.btnBack);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Live counter for feedback text
        editFeedback.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCounter.setText(s.length() + "/" + MAX_LENGTH);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Rating label under stars
        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) ->
                tvRatingLabel.setText(getRatingLabel((int) rating))
        );

        // Set initial label
        tvRatingLabel.setText(getString(R.string.select_rating));

        // Submit button
        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {
        int selectedId = rgSolved.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, R.string.feedback_select_yes_no, Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedId == R.id.rbNo) {
            // User pressed "No" → open details activity
            Intent intent = new Intent(this, SupportFeedbackDetailsActivity.class);
            intent.putExtra("rating", (int) ratingBar.getRating());
            intent.putExtra("draft", editFeedback.getText().toString().trim());
            startActivity(intent);
            return;
        }

        // User pressed "Yes" → show success dialog
        String ratingLabel = getRatingLabel((int) ratingBar.getRating());
        String freeText = editFeedback.getText().toString().trim();
        showSuccessDialog(ratingLabel, freeText);
    }

    // Maps rating number to label with emoji
    private String getRatingLabel(int rating) {
        switch (rating) {
            case 1: return getString(R.string.rating_poor);
            case 2: return getString(R.string.rating_fair);
            case 3: return getString(R.string.rating_average);
            case 4: return getString(R.string.rating_good);
            case 5: return getString(R.string.rating_excellent);
            default: return getString(R.string.select_rating);
        }
    }

    // Thank-you dialog for "Yes" flow
    private void showSuccessDialog(String ratingLabel, String freeText) {
        Dialog d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.feedback_success_dialog);
        d.setCancelable(true);

        TextView title   = d.findViewById(R.id.tvDialogTitle);
        TextView rating  = d.findViewById(R.id.tvDialogRating);
        TextView comment = d.findViewById(R.id.tvDialogText);
        Button btnClose  = d.findViewById(R.id.btnCloseDialog);

        // Title
        title.setText(R.string.feedback_thank_you);

        // Rating label
        rating.setText(ratingLabel);

        // Comment (if provided)
        if (TextUtils.isEmpty(freeText)) {
            comment.setVisibility(TextView.GONE);
        } else {
            comment.setVisibility(TextView.VISIBLE);
            comment.setText(freeText);
        }

        btnClose.setOnClickListener(v -> {
            d.dismiss();
            Toast.makeText(this, R.string.feedback_submitted, Toast.LENGTH_SHORT).show();
            finish();
        });

        d.show();
    }
}
