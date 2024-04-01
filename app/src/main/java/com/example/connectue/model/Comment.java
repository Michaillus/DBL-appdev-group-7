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

    private String publisherId;

    private String text;

    private String parentId;

    private Date timestamp;
    private String userProfilePicUrl;

    public Comment(String publisherId, String text, String parentId) {
        this(null, publisherId, text, parentId, new Date());
    }

    public Comment(String commentId, String publisherId, String text, String parentId,
                   Date timestamp) throws IllegalArgumentException {

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
        if (timestamp == null) {
            String m = "Timestamps of comment should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }

        // Setting field values of the model
        setPublisherId(publisherId);
        setText(text);
        setParentId(parentId);
        setTimestamp(timestamp);
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

    public String getUserProfilePicUrl() {return userProfilePicUrl;}

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
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserProfilePicUrl(String userProfilePicUrl) {this.userProfilePicUrl = userProfilePicUrl;}
}
