package com.example.connectue.model;

import android.util.Log;

import java.util.Date;

public class Comment {

    /**
     * Class tag for logs.
     */
    protected static final String TAG = "Comment class: ";


    /**
     * Name of publisher id field in comment collection.
     */
    public static final String PUBLISHER_ID = "userId";

    /**
     * Name of content field in comment collection.
     */
    public static final String CONTENT = "text";

    /**
     * Name of parent interactable id field in comment collection.
     */
    public static final String PARENT_ID = "parentId";

    /**
     * Name of timestamp field in comment collection.
     */
    public static final String TIMESTAMP = "timestamp";


    /**
     * FireBase id of the comment. Is defined NULL if a comment is initialized not based on the
     * FireBase document.
     */
    private String commentId;

    /**
     * FireBase id of name of publisher of a comment.
     */
    private String publisherId;

    /**
     * Text of a comment.
     */
    private String text;

    /**
     * Name of interactable to which comment is attached to.
     */
    private String parentId;

    /**
     * Date and time of creation of a comment.
     */
    private Date datetime;

    /**
     * Constructor for a comment that is not yet in the database.
     * @param publisherId Id of the publisher.
     * @param text Text of the comment.
     * @param parentId Id of parent interactable.
     */
    public Comment(String publisherId, String text, String parentId) {
        this("", publisherId, text, parentId, new Date());
    }

    /**
     * Constructor of a comment that is already in the database.
     * @param commentId Id of the comment.
     * @param publisherId Id of the publisher.
     * @param text Text of the comment.
     * @param parentId Id of parent interactable.
     * @param datetime Date and time of creation.
     * @throws IllegalArgumentException Thrown if any of the parameters are null.
     */
    public Comment(String commentId, String publisherId, String text, String parentId,
                   Date datetime) throws IllegalArgumentException {

        // Handling documents with a null field
        if (publisherId == null) {
            String m = "Comment publisher id should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }
        if (text == null) {
            String m = "Comment text should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }
        if (parentId == null) {
            String m = "Parent of comment should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }
        if (datetime == null) {
            String m = "Timestamps of comment should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }

        // Setting field values of the model
        setPublisherId(publisherId);
        setText(text);
        setParentId(parentId);
        setDatetime(datetime);
        setCommentId(commentId);
    }

    // Getter and setter for id of the comment
    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    // Getter and setter for id of the publisher of the comment
    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    // Getter and setter for the text of the comment
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // Getter and setter for id of the parent interactable of the comment
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    // Getter and setter for timestamps of the comment
    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }
}
