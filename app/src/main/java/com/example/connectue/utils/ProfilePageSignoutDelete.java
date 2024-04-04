package com.example.connectue.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.connectue.R;
import com.example.connectue.activities.LoadingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfilePageSignoutDelete {
    private final Context context;
    private final Activity activity;
    private final View view;
    private final FirebaseFirestore db;
    private final FirebaseUser user;
    private Button logoutBtn;
    private Button deleteBtn;
    private static final String LOGOUT = "Logout";
    private static final String DELTEBUTTON = "DELETE ACCOUNT";

    public ProfilePageSignoutDelete(Context context, Activity activity, View view,
                                    FirebaseFirestore db, FirebaseUser user) {
        this.view = view;
        this.context = context;
        this.activity = activity;
        this.db = db;
        this.user = user;
        initComponent();
    }
    private void initComponent() {
        logoutBtn = view.findViewById(R.id.btn_logout);
        deleteBtn = view.findViewById(R.id.btn_deleteAccount);

        initSignoutButton();
        initDeleteButton();
    }
    private void initSignoutButton() {
        logoutBtn.setText(LOGOUT);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signoutDeletePopup(true);
            }
        });
    }

    private void initDeleteButton() {
        deleteBtn.setText(DELTEBUTTON);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signoutDeletePopup(false);
            }
        });
    }

    private void signoutDeletePopup(boolean isSignout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (isSignout) {
            builder.setTitle("Logout");
            builder.setMessage("Do you want to sign out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    dialog.dismiss();
                    General.toOtherActivity(activity, LoadingActivity.class);
                }
            });
        } else {
            builder.setTitle("Delete");
            builder.setMessage("ARE YOU SURE YOU WANT TO DELETE?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteUserContents();

                    db.collection(General.USERCOLLECTION).document(user.getUid()).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseAuth.getInstance().signOut();
                                            General.toOtherActivity(activity, LoadingActivity.class);
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(activity, "Delete failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void deleteUserContents() {
        CollectionReference reviewsRef = db.collection(General.COURSEREVIEWCOLLECTION);
        CollectionReference postsRef = db.collection(General.POSTCOLLECTION);
        CollectionReference commentsRef = db.collection(General.COMMENTCOLLECTION);
        deleteUserContents(reviewsRef);
        deleteUserContents(postsRef);
        deleteUserContents(commentsRef);
    }
    private void deleteUserContents(CollectionReference collectionRef) {

        collectionRef.whereEqualTo("publisher", user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // delete posts posted by this user
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            documentSnapshot.getReference().delete();

                            // delete photos of the post that are still stored in storage
                            String photoURL = documentSnapshot.getString("photoURL");
                            if (photoURL != null){
                                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoURL);
                                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {}
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {}
                                });
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {}
                });
    }
}
