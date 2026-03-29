package com.example.smishingdetectionapp.data.model;
public class ContactUsResponse {
    private boolean success;
    private String message;
    private Data data; // nested object (only for POST / create)
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Data getData() { return data; }
    public static class Data {
        private String id;

        public String getId() { return id; }
    }
}
