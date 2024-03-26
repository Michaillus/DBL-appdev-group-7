package com.example.connectue.managers;

import android.util.Log;

import com.example.connectue.interfaces.DownloadItemCallback;
import com.example.connectue.interfaces.ItemLikeCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class LikeManager {

    /**
     * Class tag for logs.
     */
    protected String tag = "LikeManager class: ";

    /**
     * Reference to the FireStore like collection, that is assigned to the manager.
     */
    CollectionReference likeCollection;

    /**
     * Constructor for like manager.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of a collection that stores likes in the database.
     */
    public LikeManager(FirebaseFirestore db, String collectionName) {
        likeCollection = db.collection(collectionName);
    }

    /**
     * Likes the {@code documentId} interactable by the {@code userId} user if it was not liked,
     * deletes the like if the interactable
     * was already liked by the user.
     * @param documentId Id of the interactable to be liked or unliked.
     * @param userId Id of the user that likes or unlikes the interactable.
     * @param callback Callback that returns True if the interactable is now liked by the user and
     *                 False otherwise.
     */
    public void likeOrUnlike(String documentId, String userId, ItemLikeCallback callback) {

        obtainLike(documentId, userId, new DownloadItemCallback<String>() {
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
                            .addOnSuccessListener(
                                    unused -> callback.onSuccess(false))
                            .addOnFailureListener(e -> callback.onFailure(e));
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });

    }

    /**
     * Gives True if {@code documentId} interactable is liked by {@code userId} user,
     * gives False otherwise.
     * @param documentId Id of a interactable, on which like is checked.
     * @param userId Id of a user, whole like on the interactable is checked.
     * @param callback Callback that returns True or False depending on if the interactable
     *                 is liked by the user.
     */
    public void isLiked(String documentId, String userId,
                             ItemLikeCallback callback) {

        obtainLike(documentId, userId, new DownloadItemCallback<String>() {
            @Override
            public void onSuccess(String data) {
                callback.onSuccess(data != null);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });

    }

    /**
     * Queries for a like document of {@code userId} user on a {@code documentId} interactable.
     * If there is a like of the user on the interactable, returns the id of the like document.
     * Otherwise, returns {@code null}.
     * @param documentId Id of the interactable.
     * @param userId Id of the user.
     * @param callback Callback that returns id of obtained like document of {@code null}, if there
     *                 is no like.
     */
    private void obtainLike(String documentId, String userId,
                            DownloadItemCallback<String> callback) {
        Query query = likeCollection.whereEqualTo("parentId", documentId)
                .whereEqualTo("userId", userId);

        query.get().addOnSuccessListener(snapshot -> {
            if (snapshot.isEmpty()) {
                callback.onSuccess(null);
                Log.i(tag, "obtainLike onSuccess: " + documentId + " not liked by " + userId);
            } else {
                callback.onSuccess(snapshot.getDocuments().get(0).getId());
                Log.i(tag, "obtainLike onSuccess: " + documentId + " is liked by " + userId);
            }
        }).addOnFailureListener(e -> callback.onFailure(e));

    }
}
