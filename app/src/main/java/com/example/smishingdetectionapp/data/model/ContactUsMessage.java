package com.example.smishingdetectionapp.data.model;

public class ContactUsMessage {
    private String _id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String category;
    private String message;
    private String status;
    private String createdAt;
    private String updatedAt;

    // Getters
    public String getId() { return _id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getCategory() { return category; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
