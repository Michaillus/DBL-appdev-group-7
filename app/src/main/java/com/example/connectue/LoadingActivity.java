package com.example.connectue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoadingActivity extends AppCompatActivity {
    FirebaseUser user;
    FirebaseFirestore db;
    private String TAG = "LoadingUtil: ";
    Object isVerified;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DocumentReference userDoc = db.collection("users").document("UserTypes").collection("Students").document(user.getUid());
            Log.d(TAG, user.getEmail());
            userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
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
        handler.postDelayed(new Runnable() {
            public void run() {
                if (isVerified == null) {
                    Intent mInHome = new Intent(LoadingActivity.this, LoginActivity.class);
                    LoadingActivity.this.startActivity(mInHome);
                    LoadingActivity.this.finish();

                } else if (isVerified.equals(false)){
                    Intent mInHome = new Intent(LoadingActivity.this, LoginActivity.class);
                    LoadingActivity.this.startActivity(mInHome);
                    LoadingActivity.this.finish();
                } else {
                    Intent mInMain = new Intent(LoadingActivity.this, MainActivity.class);
                    LoadingActivity.this.startActivity(mInMain);
                    LoadingActivity.this.finish();
                }

            }
        }, 3000);
    }
}