package com.example.connectue.model;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;

import java.util.Date;

public abstract class Interactable {

    /**
     * Class tag for logs.
     */
    protected String TAG = "Interactable class: ";

    protected String interactableId;

    protected String publisherId;

    protected String text;

    protected long likeNumber;

    protected long dislikeNumber;

    protected long commentNumber;

    protected Date datetime;

    public Interactable(String publisherId, String text) {
        this(null, publisherId, text, 0L, 0L, 0L, new Date());
    }

    public Interactable(String interactableId, String publisherId, String text,
                        Long likeNumber, Long dislikeNumber, Long commentNumber, Date datetime) {

        // Handling null parameters
        if (publisherId == null) {
            String m = "ID of the publisher should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }
        if (text == null) {
            String m = "Post text should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }
        if (likeNumber == null) {
            String m = "Like number should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }
        if (dislikeNumber == null) {
            String m = "Dislike number should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }
        if (commentNumber == null) {
            String m = "Comment number should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }

        this.setId(interactableId);
        this.setPublisherId(publisherId);
        this.setText(text);
        this.setLikeNumber(likeNumber);
        this.setDislikeNumber(dislikeNumber);
        this.setCommentNumber(commentNumber);
        this.setDatetime(datetime);
    }

    public static CollectionReference getCollection() {
        return null;
    }


    // Getters and setters for interactable ID.
    public String getId() {
        return interactableId;
    }
    public void setId(String interactableID) {
        this.interactableId = interactableID;
    }

    //Getters and setters for publisher ID.
    public String getPublisherId() { return publisherId; }
    public void setPublisherId(String publisherId) { this.publisherId = publisherId; }

    // Getters and setters for interactable text.
    public String getText() {
        return text;
    }
    public void setText(String description) {
        this.text = description;
    }

    // Getters and setters for number of likes on the interactable.
    public long getLikeNumber() {
        return likeNumber;
    }
    public void setLikeNumber(long likeNumber) { this.likeNumber = likeNumber; }
    public void incrementLikeNumber() {
        this.likeNumber++;
    }
    public void decrementLikeNumber() {
        this.likeNumber--;
    }

    // Getters and setters for number of dislikes on the interactable.
    public long getDislikeNumber() {
        return dislikeNumber;
    }
    public void setDislikeNumber(long dislikeNumber) { this.dislikeNumber = dislikeNumber; }
    public void incrementDislikeNumber() {
        this.dislikeNumber++;
    }
    public void decrementDislikeNumber() {
        this.dislikeNumber--;
    }

    // Getters and setters for number of comments on the post.
    public long getCommentNumber() {
        return commentNumber;
    }
    public void setCommentNumber(long commentNumber) { this.commentNumber = commentNumber; }
    public void incrementCommentNumber() {
        this.commentNumber++;
    }
    public void decrementCommentNumber() {
        this.commentNumber--;
    }

    // Getters and setters for datetime.
    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

}
