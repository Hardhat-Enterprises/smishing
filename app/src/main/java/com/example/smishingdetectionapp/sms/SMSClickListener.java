package com.example.smishingdetectionapp.sms;

import com.example.smishingdetectionapp.data.model.SMSMessage;

public interface SMSClickListener {
    void OnMessageClicked(SMSMessage message);
}
