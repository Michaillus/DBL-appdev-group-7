package com.example.connectue.managers;

import android.nfc.Tag;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.interfaces.FireStoreLikeCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LikeManager {

    /**
     * Class tag for logs.
     */
    protected String TAG = "LikeManager class: ";

    /**
     * Reference to the firebase collection, that is assigned to the manager.
     */
    CollectionReference likeCollection;

    public LikeManager(FirebaseFirestore db, String collection) {
        likeCollection = db.collection(collection);
    }

    public void likeOrUnlike(String documentId, String userId, FireStoreLikeCallback callback) {
        obtainLike(documentId, userId, new FireStoreDownloadCallback<String>() {
            @Override
            public void onSuccess(String likeId) {
                if (likeId == null) {
                    // Document is not liked by the user.
                    Map<String,Object> likeData = new HashMap<>();
                    likeData.put("parentId", documentId);
                    likeData.put("userId", userId);

                    likeCollection.add(likeData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            callback.onSuccess(true);
                        }
                    }).addOnFailureListener(e -> callback.onFailure(e));
                } else {
                    // Document is liked by the user.
                    likeCollection.document(likeId).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            callback.onSuccess(false);
                        }
                    }).addOnFailureListener(e -> callback.onFailure(e));
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void isLiked(String documentId, String userId,
                             FireStoreLikeCallback callback) {
        obtainLike(documentId, userId, new FireStoreDownloadCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data == null) {
                    callback.onSuccess(false);
                } else {
                    callback.onSuccess(true);
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    private void obtainLike(String documentId, String userId,
                            FireStoreDownloadCallback<String> callback) {
        Query query = likeCollection.whereEqualTo("parentId", documentId)
                .whereEqualTo("userId", userId);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                if (snapshot.isEmpty()) {
                    callback.onSuccess(null);
                    Log.d(TAG, "obtainLike onSuccess: " + documentId + " not liked by " + userId);
                } else {
                    callback.onSuccess(snapshot.getDocuments().get(0).getId());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
