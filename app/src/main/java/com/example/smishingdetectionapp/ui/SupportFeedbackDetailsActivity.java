package com.example.smishingdetectionapp.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.R;

import java.util.ArrayList;
import java.util.List;

public class SupportFeedbackDetailsActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 280;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_feedback_details);

        // Views
        CheckBox cbSolution = findViewById(R.id.cbSolution);
        CheckBox cbRobotic = findViewById(R.id.cbRobotic);
        CheckBox cbSlow = findViewById(R.id.cbSlow);
        CheckBox cbWrongWords = findViewById(R.id.cbWrongWords);
        CheckBox cbRepetitive = findViewById(R.id.cbRepetitive);
        CheckBox cbUnfriendly = findViewById(R.id.cbUnfriendly);

        EditText editFeedback = findViewById(R.id.editFeedbackDetails);
        TextView tvCounter = findViewById(R.id.tvCounterDetails);
        Button btnSubmit = findViewById(R.id.btnSubmitDetails);
        ImageButton btnBack = findViewById(R.id.btnBackDetails);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Live counter
        editFeedback.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCounter.setText(s.length() + "/" + MAX_LENGTH);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Submit button
        btnSubmit.setOnClickListener(v -> {
            List<String> issues = new ArrayList<>();
            if (cbSolution.isChecked()) issues.add(getString(R.string.problem_solution_not_helpful));
            if (cbRobotic.isChecked()) issues.add(getString(R.string.problem_robotic_replies));
            if (cbSlow.isChecked()) issues.add(getString(R.string.problem_slow_response));
            if (cbWrongWords.isChecked()) issues.add(getString(R.string.problem_wrong_words));
            if (cbRepetitive.isChecked()) issues.add(getString(R.string.problem_repetitive));
            if (cbUnfriendly.isChecked()) issues.add(getString(R.string.problem_unfriendly));

            String feedbackText = editFeedback.getText().toString().trim();

            // Build issues summary string
            StringBuilder issuesSummary = new StringBuilder();
            for (String issue : issues) {
                issuesSummary.append("• ").append(issue).append("\n");
            }

            // Show apology dialog
            showApologyDialog(issuesSummary.toString().trim(), feedbackText);
        });
    }

    private void showApologyDialog(String issues, String freeText) {
        Dialog d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.feedback_success_dialog); // reuse same layout
        d.setCancelable(true);

        TextView title   = d.findViewById(R.id.tvDialogTitle);
        TextView rating  = d.findViewById(R.id.tvDialogRating);
        TextView comment = d.findViewById(R.id.tvDialogText);
        Button btnClose  = d.findViewById(R.id.btnCloseDialog);

        // Change title for "No" case
        title.setText(R.string.feedback_apology);

        // Show selected problems
        if (TextUtils.isEmpty(issues)) {
            rating.setText(getString(R.string.no_specific_issue));
        } else {
            rating.setText(issues);
        }

        // Show user comment
        if (TextUtils.isEmpty(freeText)) {
            comment.setVisibility(TextView.GONE);
        } else {
            comment.setVisibility(TextView.VISIBLE);
            comment.setText(freeText);
        }

        btnClose.setOnClickListener(v -> {
            d.dismiss();
            finish();
        });

        d.show();
    }
}
