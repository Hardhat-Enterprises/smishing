package com.example.smishingdetectionapp.DataBase;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BackupCodesResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    // API returns plaintext codes once
    @SerializedName("codes")
    private List<String> codes;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<String> getCodes() { return codes; }
}
