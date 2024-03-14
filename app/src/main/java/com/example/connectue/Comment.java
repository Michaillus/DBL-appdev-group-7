package com.example.connectue;

import java.sql.Timestamp;

public class Comment {
    private String userId;
    private String content;
    private String parentId;
    private Timestamp timeStamp;
    private String commentId;


    protected Comment(String userId, String content, String parentId, Timestamp timeStamp, String commentId) {
        this.userId = userId;
        this.content = content;
        this.parentId = parentId;
        this.timeStamp = timeStamp;
        this.commentId = commentId;
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
