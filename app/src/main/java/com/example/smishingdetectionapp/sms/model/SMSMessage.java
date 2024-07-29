package com.example.smishingdetectionapp.sms.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SMSMessage implements Parcelable {
    private String sender;
    private String body;

    public SMSMessage(String sender, String body) {
        this.sender = sender;
        this.body = body;
    }

    protected SMSMessage(Parcel in) {
        sender = in.readString();
        body = in.readString();
    }

    public static final Creator<SMSMessage> CREATOR = new Creator<SMSMessage>() {
        @Override
        public SMSMessage createFromParcel(Parcel in) {
            return new SMSMessage(in);
        }

        @Override
        public SMSMessage[] newArray(int size) {
            return new SMSMessage[size];
        }
    };

    public String getSender() {
        return sender;
    }

    public String getBody() {
        return body;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(sender);
        dest.writeString(body);
    }
}
