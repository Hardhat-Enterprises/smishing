package com.example.smishingdetectionapp;


import java.util.List;

public class Role {
    private String roleName;
    private List<String> permissions;

    public Role(String roleName, List<String> permissions) {
        this.roleName = roleName;
        this.permissions = permissions;
    }

    public String getRoleName() {
        return roleName;
    }

    public List<String> getPermissions() {
        return permissions;
    }



}

