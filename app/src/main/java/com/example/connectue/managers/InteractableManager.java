package com.example.connectue.managers;

import android.util.Log;

import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.interfaces.FireStoreLikeCallback;
import com.example.connectue.interfaces.FireStoreUploadCallback;
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
    protected LikeManager likeManager;

    /**
     * Like manager that supports database requests regarding disliking an interactable
     * of model {@code T}.
     */
    protected LikeManager dislikeManager;

    /**
     * Comment manager that supports database requests regarding commenting an interactable
     * of model {@code T}.
     */
    protected CommentManager commentManager;

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

        tag = "InteractableManager class: ";
    }

    /**
     * Likes the interactable, if it was not liked or removes the like from the interactable,
     * if it was already liked.
     * @param interactable Interactable to like or unlike.
     * @param userId Id of the user who likes or unlikes the interactable.
     */
    public void likeOrUnlike(T interactable, String userId, FireStoreLikeCallback callback) {
        likeManager.likeOrUnlike(interactable.getId(), userId, new FireStoreLikeCallback() {
            @Override
            public void onSuccess(Boolean isLiked) {
                if (isLiked) {
                    interactable.incrementLikeNumber();
                    update(interactable.getId(), "likes", interactable.getLikeNumber(), () -> {
                        callback.onSuccess(true);
                    });
                } else {
                    interactable.decrementLikeNumber();
                    update(interactable.getId(), "likes", interactable.getLikeNumber(), () -> {
                        callback.onSuccess(false);
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(tag, "Error while liking", e);
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

    /**
     * Dislikes the interactable, if it was not disliked or removes the dislike from the interactable,
     * if it was already disliked.
     * @param interactable Interactable to dislike or undislike.
     * @param userId Id of the user who likes or unlikes the interactable.
     */
    public void dislikeOrUndislike(T interactable, String userId, FireStoreLikeCallback callback) {
        dislikeManager.likeOrUnlike(interactable.getId(), userId, new FireStoreLikeCallback() {
            @Override
            public void onSuccess(Boolean isDisiked) {
                if (isDisiked) {
                    interactable.incrementDislikeNumber();
                    update(interactable.getId(), "dislikes", interactable.getDislikeNumber(), () -> {
                        callback.onSuccess(true);
                    });
                } else {
                    interactable.decrementDislikeNumber();
                    update(interactable.getId(), "dislikes", interactable.getDislikeNumber(), () -> {
                        callback.onSuccess(false);
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(tag, "Error while disliking", e);
            }
        });
    }

    /**
     * Checks if the interactable was disliked by user or not. Calls {@code callback.onSuccess} and
     * returns if interactable is disliked when finished.
     * @param interactableId Id of the interactable.
     * @param userId Id of the user.
     * @param callback Callback to return if interactable is disliked.
     */
    public void isDisliked(String interactableId, String userId, FireStoreLikeCallback callback) {
        dislikeManager.isLiked(interactableId, userId, callback);
    }

    /**
     * Retrieves {@code amount} of most recent comments of {@code parentId} interactable,
     * after the last uploaded one.
     * @param parentId Id of interactable which comments to retrieve.
     * @param amount Number of comments to retrieve
     * @param callback Callback that passes a list of downloaded comments when finished downloading
     *                 or passes the error message on failure.
     */
    public void downloadRecentComments(String parentId, int amount,
                                       FireStoreDownloadCallback<List<Comment>> callback) {
        commentManager.downloadRecent(parentId, amount, callback);
    }

    /**
     * Uploads a comment for the interactable to the database.
     * @param comment Instance of a comment to publish.
     * @param callback Callback that is called when the upload process is finished.
     */
    public void uploadComment(Comment comment, FireStoreUploadCallback callback) {
        commentManager.upload(comment, callback);
    }
}
