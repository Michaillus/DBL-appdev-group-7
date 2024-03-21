package com.example.connectue.model;

import android.util.Log;

import com.example.connectue.interfaces.CommentCreateCallback;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class Comment {
    private String userId;
    private String content;
    private String parentId;
    private Date timestamp;
    private String commentId;
    private String publisherName;
    private String userProfilePicUrl;

    private static final String TAG = "Comment class: ";

    public Comment() {
        // Default
    }

    public Comment(String userId, String content, String parentId, Date timestamp) {
        this.userId = userId;
        this.content = content;
        this.parentId = parentId;
        this.timestamp = timestamp;
    }

    public static void createComment(DocumentSnapshot document, CommentCreateCallback callback) {
        // Handling documents with a null field
        if (document.getString("userId") == null) {
            String m = "Comment userId should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }
        if (document.getString("content") == null) {
            String m = "Comment content should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }
        if (document.getString("parentId") == null) {
            String m = "parent of comment should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }
        if (document.getLong("comments") == null) {
            String m = "Number of post comments should not be null";
            Log.e(TAG, m);
            throw new IllegalArgumentException(m);
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public String getParentId() {
        return parentId;
    }

    public Date gettimestamp() {
        return timestamp;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public String getUserProfilePicUrl() {return userProfilePicUrl;}

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public void setUserProfilePicUrl(String userProfilePicUrl) {this.userProfilePicUrl = userProfilePicUrl;}
}
