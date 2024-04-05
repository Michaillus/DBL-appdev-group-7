package com.example.connectue.managers;

import com.example.connectue.model.User;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserManager extends EntityManager<User> {

    /**
     * Constructor for a manager of users.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of the collection that stores user in the database.
     */
    public UserManager(FirebaseFirestore db, String collectionName) {
        super(db, collectionName);

        tag = "UserManager";
    }

    /**
     * Converts a FireBase document snapshot of the user collection into an instance
     * of user model.
     * @param document FireBase document snapshot of the user collection.
     * @return Instance of the user model.
     */
    @Override
    public User deserialize(DocumentSnapshot document) {
        return new User(
                document.getId(),
                document.getString("firstName"),
                document.getString("lastName"),
                document.getBoolean("isVerified"),
                //document.getString("password"),
                document.getString("email"),
                document.getString("phone"),
                document.getString("profilePicURL"),
                document.getString("program"),
                document.getLong("role").intValue()
        );
    }

    /**
     * Converts an instance of the user model to a corresponding map for uploading to the
     * user collection.
     * @param user Instance of the user model.
     * @return Map for uploading to user collection.
     */
    @Override
    public Map<String, Object> serialize(User user) {
        Map<String, Object> userData = new HashMap<>();

        userData.put("timestamp", new Timestamp(new Date()));
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("isVerified", user.isVerified());
        //userData.put("password", user.getPassword());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhoneNumber());
        userData.put("profilePicUrl", user.getProfilePicUrl());
        userData.put("program", user.getProgram());
        userData.put("role", user.getRole());

        userData.put("userCourses", new ArrayList<String>());

        return userData;
    }
}
