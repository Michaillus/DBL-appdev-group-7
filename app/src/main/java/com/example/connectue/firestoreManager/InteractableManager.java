package com.example.connectue.firestoreManager;

import android.util.Log;

import com.example.connectue.interfaces.FireStoreLikeCallback;
import com.example.connectue.model.Interactable;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class InteractableManager<T extends Interactable> extends EntityManager<T> {

    private final static String TAG = "InteractableManager class: ";

    LikeManager<T> likeManager;
    LikeManager<T> dislikeManager;
    public InteractableManager(FirebaseFirestore db, String collectionName,
                               String likeCollectionName, String dislikeCollectionName) {
        super(db, collectionName);
        likeManager = new LikeManager<T>(db, likeCollectionName);
        dislikeManager = new LikeManager<T>(db, dislikeCollectionName);
    }

    public void likeOrUnlike(T interactable, String userId, FireStoreLikeCallback callback) {
        likeManager.likeOrUnlike(interactable.getId(), userId, new FireStoreLikeCallback() {
            @Override
            public void onSuccess(Boolean isLiked) {
                if (isLiked) {
                    interactable.incrementLikeNumber();
                    callback.onSuccess(true);
                } else {
                    interactable.decrementLikeNumber();
                    callback.onSuccess(false);
                }
                update(interactable.getId(), "likes", interactable.getLikeNumber(), () -> {});
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while liking", e);
            }
        });
    }

    public void isLiked(String interactableId, String userId, FireStoreLikeCallback callback) {
        likeManager.isLiked(interactableId, userId, callback);
    }
}
