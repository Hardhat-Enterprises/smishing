package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.sms.SMSAdapter;
import com.example.smishingdetectionapp.sms.SMSClickListener;
import com.example.smishingdetectionapp.sms.model.SMSMessage;

import java.util.ArrayList;
import java.util.List;

public class SmsActivity extends AppCompatActivity implements SMSClickListener {
    RecyclerView smsRecyclerView; // Holds the recyclerview which displays the news list
    SMSAdapter smsAdapter;

    List<SMSMessage> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sms);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(v -> {
            finish();
        });

        smsRecyclerView = findViewById(R.id.messages_recycler_view);
        smsRecyclerView.setHasFixedSize(true); // Set to improve performance since changes in content do not change layout size
        smsRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set layout manager for RecyclerView
        smsAdapter = new SMSAdapter(this); // Initialize the adapter with context
        smsRecyclerView.setAdapter(smsAdapter);

        messageList.add(new SMSMessage("Ulindu", "Hello, how are you doing?"));
        messageList.add(new SMSMessage("Teacher", "Have you submitted your assignments? You need to do that as soon as possible!"));
        messageList.add(new SMSMessage("Team Lead", "I have seen your work. It looks good."));
        messageList.add(new SMSMessage("Team Member", "Can you help me with the Android thing? I am a bit confused."));

        smsAdapter.updateMessagesList(messageList);
    }

    @Override
    public void OnMessageClicked(SMSMessage message) {
        Intent intent = new Intent(SmsActivity.this, SMSMessageDetailActivity.class);
        intent.putExtra("SMS_MESSAGE", message);
        startActivity(intent);
    }
}