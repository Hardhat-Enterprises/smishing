package com.example.smishingdetectionapp.DataBase;

// Class to handle the response from signup operations
public class SignupResponse {

    private String message;
    private String token;
    private int userId;

    private boolean exists;

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(String userId) { this.userId = Integer.parseInt(userId); }



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isExists() {return exists;}

    public void setExists(boolean exists) {
        this.exists = exists;
    }
}

