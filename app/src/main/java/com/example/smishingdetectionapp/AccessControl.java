package com.example.smishingdetectionapp;

//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import org.bson.Document;
//import static com.mongodb.client.model.Filters.eq;

//public class AccessControl {
//    private MongoCollection<Document> usersCollection;
//    private MongoCollection<Document> rolesCollection;

//    public AccessControl() {
        // MongoDB 연결 가져오기
        //MongoDatabase database = DB_Connection.js.getDatabase();  // 수정된 DBConnection 사용
        //usersCollection = database.getCollection("users");
        //rolesCollection = database.getCollection("roles");
//    }

    // 사용자가 권한을 가지고 있는지 확인하는 메서드
//    public boolean hasPermission(String email, String requiredPermission) {
//        Document userDoc = usersCollection.find(eq("email", email)).first();
//        if (userDoc == null) {
//            System.out.println("사용자를 찾을 수 없습니다.");
//            return false;
//        }

 //       String roleId = userDoc.getString("roleId");
 //       Document roleDoc = rolesCollection.find(eq("_id", roleId)).first();
 //       if (roleDoc == null) {
 //           System.out.println("역할을 찾을 수 없습니다.");
  //          return false;
   //     }

//        Role role = Role.fromDocument(roleDoc);
//        return role.getPermissions().contains(requiredPermission);
//    }
//}



//import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public class AccessControl {
    private MongoCollection<Document> rolesCollection;

    public AccessControl() {
        // MongoDB connection setting
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:3000");
        MongoDatabase database = mongoClient.getDatabase("yourDatabaseName"); // 데이터베이스 이름 변경
        rolesCollection = database.getCollection("roles");
    }

    // checking user has minimum permission
    public boolean hasPermission(String userId, String requiredPermission) {
        Document userRole = rolesCollection.find(new Document("userId", userId)).first();
        if (userRole != null) {
            List<String> permissions = (List<String>) userRole.get("permissions");
            return permissions.contains(requiredPermission);
        }

        return false;
    }

}
