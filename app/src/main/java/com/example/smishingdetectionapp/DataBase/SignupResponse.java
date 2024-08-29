package com.example.smishingdetectionapp.DataBase;

// Class to handle the response from signup operations
public class SignupResponse {

    private String message;
    private String token;

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

