package com.example.connectue.firestoreManager;

import com.example.connectue.model.User2;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserManager extends EntityManager<User2> {

    public UserManager(FirebaseFirestore db, String collectionName) {
        super(db, collectionName);
    }

    @Override
    protected User2 deserialize(DocumentSnapshot document) {
        return new User2(
                document.getId(),
                document.getString("firstName"),
                document.getString("lastName"),
                document.getBoolean("isVerified"),
                document.getString("password"),
                document.getString("phone"),
                document.getString("profilePicURL"),
                document.getString("program"),
                document.getLong("role").intValue()
        );
    }

    @Override
    protected Map<String, Object> serialize(User2 user) {
        Map<String, Object> userData = new HashMap<>();

        userData.put("timestamp", new Timestamp(new Date()));
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("isVerified", user.isVerified());
        userData.put("password", user.getPassword());
        userData.put("phone", user.getPhoneNumber());
        userData.put("profilePicUrl", user.getProfilePicUrl());
        userData.put("program", user.getProgram());
        userData.put("role", user.getRole());

        return userData;
    }
}
