package com.example.smishingdetectionapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.data.model.ContactUsResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.smishingdetectionapp.DataBase.ApiClient;


public class ContactUsActivity extends AppCompatActivity {

    private EditText email, firstName, lastName, company, phone, message;
    private CheckBox cb1, cb2, cb3, cb4;
    private Button submitButton;
    private Retrofitinterface retrofitInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        // Input fields
        email = findViewById(R.id.email);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        company = findViewById(R.id.company);
        phone = findViewById(R.id.phone);
        message = findViewById(R.id.message);

        // Checkboxes
        cb1 = findViewById(R.id.checkbox_fake_sms);
        cb2 = findViewById(R.id.checkbox_feature_request);
        cb3 = findViewById(R.id.checkbox_feedback);
        cb4 = findViewById(R.id.checkbox_other);

        // Submit button
        submitButton = findViewById(R.id.submitContact);

        // Back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Action bar setup
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Contact Us");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set up real-time validation
        setupValidation();

        // ...
        // Retrofit setup with deployed backend
        retrofitInterface = ApiClient
                .getClient("https://smishing-backend-1004745454775.australia-southeast1.run.app/")
                .create(Retrofitinterface.class);


        // Submit button logic
        submitButton.setOnClickListener(v -> validateAndSubmit());
    }

    private void setupValidation() {
        email.addTextChangedListener(new GenericTextWatcher(email, true));
        firstName.addTextChangedListener(new GenericTextWatcher(firstName, false));
        lastName.addTextChangedListener(new GenericTextWatcher(lastName, false));
        company.addTextChangedListener(new GenericTextWatcher(company, false));
        message.addTextChangedListener(new GenericTextWatcher(message, false));
    }

    private class GenericTextWatcher implements TextWatcher {
        private final EditText field;
        private final boolean isEmail;

        public GenericTextWatcher(EditText field, boolean isEmail) {
            this.field = field;
            this.isEmail = isEmail;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isEmail) {
                if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    field.setError("Enter a valid email");
                } else {
                    field.setError(null);
                }
            } else {
                if (s.toString().trim().isEmpty()) {
                    field.setError("This field is required");
                } else {
                    field.setError(null);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }

    private void validateAndSubmit() {
        boolean isValid = true;

        // Get all field values
        String emailText = email.getText().toString().trim();
        String firstNameText = firstName.getText().toString().trim();
        String lastNameText = lastName.getText().toString().trim();
        String companyText = company.getText().toString().trim();
        String phoneText = phone.getText().toString().trim();
        String messageText = message.getText().toString().trim();

        // Email
        if (emailText.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Valid email is required");
            isValid = false;
        }

        if (firstNameText.isEmpty()) {
            firstName.setError("First name is required");
            isValid = false;
        }

        if (lastNameText.isEmpty()) {
            lastName.setError("Last name is required");
            isValid = false;
        }

        if (companyText.isEmpty()) {
            company.setError("Company is required");
            isValid = false;
        }

        if (messageText.isEmpty()) {
            message.setError("Message is required");
            isValid = false;
        }

        if (!cb1.isChecked() && !cb2.isChecked() && !cb3.isChecked() && !cb4.isChecked()) {
            Toast.makeText(this, "Please select at least one request type", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isValid) {
            sendMessageToBackend(emailText, firstNameText, lastNameText, companyText, phoneText, messageText);
        }
    }

    private void sendMessageToBackend(String email, String firstName, String lastName, String company,
                                      String phone, String message) {
        HashMap<String, String> map = new HashMap<>();
        map.put("fullName", firstName + " " + lastName);
        map.put("email", email);
        map.put("phoneNumber", phone);
        map.put("category", getSelectedCategory());
        map.put("message", message);
        map.put("appVersion", "1.0.0");
        map.put("deviceInfo", "Android Device");

        Call<ContactUsResponse> call = retrofitInterface.sendContactMessage(map);

        call.enqueue(new Callback<ContactUsResponse>() {
            @Override
            public void onResponse(Call<ContactUsResponse> call, Response<ContactUsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ContactUsActivity.this, "Message Sent!", Toast.LENGTH_LONG).show();
                    clearForm();

                    // Optional: go to a "submission success" screen
                    Intent intent = new Intent(ContactUsActivity.this, Submissionscreen.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ContactUsActivity.this, "Failed: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ContactUsResponse> call, Throwable t) {
                Toast.makeText(ContactUsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getSelectedCategory() {
        if (cb1.isChecked()) return "bug";           // backend expects "bug"
        if (cb2.isChecked()) return "support";       // mapping categories
        if (cb3.isChecked()) return "feedback";
        if (cb4.isChecked()) return "other";
        return "other";
    }

    private void clearForm() {
        email.setText("");
        firstName.setText("");
        lastName.setText("");
        company.setText("");
        phone.setText("");
        message.setText("");
        cb1.setChecked(false);
        cb2.setChecked(false);
        cb3.setChecked(false);
        cb4.setChecked(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
