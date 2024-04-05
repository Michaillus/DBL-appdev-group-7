package com.example.connectue.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.connectue.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * The application first opens on this page.
 * Here we decide whether the user is logged in or not.
 */
public class LoadingActivity extends AppCompatActivity {
    FirebaseUser user;
    FirebaseFirestore db;
    private String TAG = "LoadingUtil: ";
    Object isVerified;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        /**
         * get the instances of firestore and fireauth.
         */
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        /**
         * Get user details in the event that a user
         * has already logged into the application
         */
        if (user != null) {
            DocumentReference userDoc = db.collection("users").document(user.getUid());
            Log.d(TAG, user.getEmail());
            /**
             * the following snippet of code is used
             * to determine whether or not the user is verified.
             */
            userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        /**
                         * Check if user has verified his email through email authentication.
                         */
                        if (document.exists()) {
                            // Get the value of isVerified
                            isVerified = document.get("isVerified");
                            if (isVerified != null) {
                                Log.d(TAG, "Value of field 'isVerified': " + isVerified.toString());
                            } else {
                                Log.d(TAG, "Field 'isVerified' is null.");
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }




        final Handler handler = new Handler();
        /**
         * handle page redirection for different user cases.
         */
        handler.postDelayed(new Runnable() {
            public void run() {
                /**
                 * if user is verified redirect to home page
                 */
                if (isVerified == null) {
                    Intent mInHome = new Intent(LoadingActivity.this, LoginActivity.class);
                    LoadingActivity.this.startActivity(mInHome);
                    LoadingActivity.this.finish();
                    /**
                     * if user is not verified but has
                     * an account, redirect to login page
                      */
                } else if (isVerified.equals(false)){
                    Intent mInHome = new Intent(LoadingActivity.this, LoginActivity.class);
                    LoadingActivity.this.startActivity(mInHome);
                    LoadingActivity.this.finish();
                    /**
                     * otherwise redirect to login page.
                     */
                } else {
                    Intent mInMain = new Intent(LoadingActivity.this, MainActivity.class);
                    LoadingActivity.this.startActivity(mInMain);
                    LoadingActivity.this.finish();
                }

            }
        }, 3000);
    }
}