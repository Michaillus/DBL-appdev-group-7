package com.example.connectue;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public abstract class User {

    private static final String TAG = "User class ";

    public String userId;
    public String email;
    public Boolean isVerified;
    public String password;
    public String firstName;
    public String lastName;
    public int role;
    public String phone;

    public static void fetchUserName(String uid, UserNameCallback callback)
            throws IllegalArgumentException, IllegalStateException {
        if (uid == null) {
            String m = "user id must not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Retrieve the user name from the document snapshot
                String userName = documentSnapshot.getString("firstName") + " " + documentSnapshot.getString("lastName");
                Log.d("User Name", "User name: " + userName);
                // Invoke the callback with the retrieved username
                callback.onUserNameFetched(userName);
            } else {
                String m = "User document does not exist";
                Log.e(TAG, m);
                throw new IllegalStateException(m);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            throw new IllegalStateException(e.getMessage());
        });
    }
}
