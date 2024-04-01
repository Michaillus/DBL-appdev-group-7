package com.example.connectue.model;

import android.util.Log;

import java.util.Date;

public class Reply {
    /**
     * Class tag for logs.
     */
    protected static final String TAG = "Reply class: ";


    /**
     * Name of publisher id field in reply collection.
     */
    public static final String PUBLISHER_ID = "userId";

    /**
     * Name of content field in reply collection.
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
    private String replyId;

    private String publisherId;

    private String text;

    private String parentId;

    private Date timestamp;


    public Reply(String replyId, String publisherId, String text, String parentId,
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
        setReplyId(replyId);


    }



    public String getReplyId() { return replyId; }
    public void setReplyId(String replyId) { this.replyId = replyId; }

    public String getPublisherId() { return publisherId; }

    public void setPublisherId(String publisherId) { this.publisherId = publisherId; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public Date getTimestamp() { return timestamp; }

    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
