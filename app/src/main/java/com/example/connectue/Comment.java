package com.example.connectue;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import java.sql.Timestamp;

public class Comment {
    private String userId;
    private String content;
    private String parentId;
    private Timestamp timeStamp;
    private String commentId;

    private static final String TAG = "Comment class: ";



    protected Comment(String userId, String content, String parentId, Timestamp timeStamp, String commentId) {
        this.userId = userId;
        this.content = content;
        this.parentId = parentId;
        this.timeStamp = timeStamp;
        this.commentId = commentId;
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

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
