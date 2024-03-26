package com.example.connectue.model;

import android.util.Log;

import java.util.Date;

public abstract class Interactable {

    /**
     * Class tag for logs.
     */
    protected String tag = "Interactable model";

    /**
     * Id of the interactable in a corresponding table in the database.
     */
    protected String interactableId;

    /**
     * Id of the publisher of interactable in the user collection in the database.
     */
    protected String publisherId;

    /**
     * Main text of the interactable.
     */
    protected String text;

    /**
     * Number of likes on the interactable.
     */
    protected long likeNumber;

    /**
     * Number of dislikes on the interactable.
     */
    protected long dislikeNumber;

    /**
     * Number of comments on the interactable.
     */
    protected long commentNumber;

    /**
     * Date and time of publishing of the interactable.
     */
    protected Date datetime;

    /**
     * Constructor for an interactable that is not yet in the database.
     * @param publisherId Database id of the publisher.
     * @param text Main text of the interactable.
     */
    public Interactable(String publisherId, String text) {
        this(null, publisherId, text, 0L, 0L, 0L, new Date());
    }

    /**
     * Constructor for an interactable that is taken from the database.
     * @param interactableId Database id of the interactable.
     * @param publisherId Database id of the publisher.
     * @param text Main text.
     * @param likeNumber Number of likes.
     * @param dislikeNumber Number of dislikes.
     * @param commentNumber Number of comments.
     * @param datetime Date and time of publication.
     */
    public Interactable(String interactableId, String publisherId, String text,
                        Long likeNumber, Long dislikeNumber, Long commentNumber, Date datetime) {

        // Handling null parameters
        if (publisherId == null) {
            String m = "ID of the publisher should not be null";
            Log.e(tag, m);
            throw new IllegalArgumentException(m);
        }
        if (text == null) {
            String m = "Post text should not be null";
            Log.e(tag, m);
            throw new IllegalArgumentException(m);
        }
        if (likeNumber == null) {
            String m = "Like number should not be null";
            Log.e(tag, m);
            throw new IllegalArgumentException(m);
        }
        if (dislikeNumber == null) {
            String m = "Dislike number should not be null";
            Log.e(tag, m);
            throw new IllegalArgumentException(m);
        }
        if (commentNumber == null) {
            String m = "Comment number should not be null";
            Log.e(tag, m);
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
