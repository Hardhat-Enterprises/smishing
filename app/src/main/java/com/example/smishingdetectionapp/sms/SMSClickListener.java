package com.example.smishingdetectionapp.sms;

import com.example.smishingdetectionapp.sms.model.SMSMessage;

public interface SMSClickListener {
    void OnMessageClicked(SMSMessage message);
}
