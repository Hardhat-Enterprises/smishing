package com.example.smishingdetectionapp;

//import org.bson.Document;
//import java.util.List;

//public class Role {
//    private String roleName;
//    private List<String> permissions;

    // 생성자
//    public Role(String roleName, List<String> permissions) {
//        this.roleName = roleName;
//        this.permissions = permissions;
//    }

    // Role 객체를 MongoDB의 Document로 변환
//    public Document toDocument() {
//        return new Document("roleName", roleName)
//                .append("permissions", permissions);
//    }

    // MongoDB Document에서 Role 객체로 변환
//    public static Role fromDocument(Document doc) {
//        String roleName = doc.getString("roleName");
//        List<String> permissions = (List<String>) doc.get("permissions");
//        return new Role(roleName, permissions);
//    }

//    public String getRoleName() {
//        return roleName;
//    }

//    public List<String> getPermissions() {
//        return permissions;
//    }
//}


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

// 역할 및 권한 예시
//List<String> adminPermissions = Arrays.asList("READ", "WRITE", "DELETE");
//Role adminRole = new Role("Admin", adminPermissions);

//List<String> editorPermissions = Arrays.asList("READ", "WRITE");
//Role editorRole = new Role("Editor", editorPermissions);

//List<String> viewerPermissions = Arrays.asList("READ");
//Role viewerRole = new Role("Viewer", viewerPermissions);