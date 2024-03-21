package com.example.connectue.managers;

import android.util.Log;

import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.interfaces.FireStoreLikeCallback;
import com.example.connectue.model.Comment;
import com.example.connectue.model.Interactable;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Database requests manager for model that is a subclass of interactable. Besides database requests
 * connected to the collection of the model documents, it supports database requests for liking,
 * disliking and commenting of objects of the model.
 * @param <T> Model class corresponding to the documents of the target collection, like, dislike
 *           and comment collections.
 */
public abstract class InteractableManager<T extends Interactable> extends EntityManager<T> {

    /**
     * Like manager that supports database requests regarding liking an interactable
     * of model {@code T}.
     */
    LikeManager likeManager;

    /**
     * Like manager that supports database requests regarding disliking an interactable
     * of model {@code T}.
     */
    LikeManager dislikeManager;

    /**
     * Comment manager that supports database requests regarding commenting an interactable
     * of model {@code T}.
     */
    CommentManager commentManager;

    /**
     * Constructor for manager of interactables.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of collection that stores {@code T} model documents
     *                       in the database.
     * @param likeCollectionName Name of collection that stores {@code T} model likes
     *                           in the database.
     * @param dislikeCollectionName Name of collection that stores {@code T} model dislikes
     *                           in the database.
     * @param commentCollectionName Name of collection that stores {@code T} model comments
      *                           in the database.
     */
    public InteractableManager(FirebaseFirestore db, String collectionName,
                               String likeCollectionName, String dislikeCollectionName,
                               String commentCollectionName) {
        super(db, collectionName);
        likeManager = new LikeManager(db, likeCollectionName);
        dislikeManager = new LikeManager(db, dislikeCollectionName);
        commentManager = new CommentManager(db, commentCollectionName);

        TAG = "InteractableManager class: ";
    }

    /**
     * Likes the interactable, if it was not liked or removes the like from the interactable,
     * if it was already liked.
     * @param interactable Interactable to like or unlike.
     * @param userId Id of the user who likes or unlikes the interactable.
     */
    public void likeOrUnlike(T interactable, String userId) {
        likeManager.likeOrUnlike(interactable.getId(), userId, new FireStoreLikeCallback() {
            @Override
            public void onSuccess(Boolean isLiked) {
                if (isLiked) {
                    interactable.incrementLikeNumber();
                } else {
                    interactable.decrementLikeNumber();
                }
                update(interactable.getId(), "likeNumber", interactable.getLikeNumber(), () -> {});
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while liking", e);
            }
        });
    }

    /**
     * Checks if the interactable was liked by user or not. Calls {@code callback.onSuccess} and
     * returns if interactable is liked when finished.
     * @param interactableId Id of the interactable.
     * @param userId Id of the user.
     * @param callback Callback to return if interactable is liked.
     */
    public void isLiked(String interactableId, String userId, FireStoreLikeCallback callback) {
        likeManager.isLiked(interactableId, userId, callback);
    }

    public void downloadRecentComments(String parentId, int amount,
                                       FireStoreDownloadCallback<List<Comment>> callback) {
        commentManager.downloadRecent(parentId, amount, callback);
    }
}
