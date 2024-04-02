package com.example.connectue.managers;

import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.model.Comment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentManager extends EntityManager<Comment> {

    /**
     * Constructor for comment manager.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of collection that stores comments in the database.
     */
    public CommentManager(FirebaseFirestore db, String collectionName) {
        super(db, collectionName);

        tag = "CommentManager";
    }

    /**
     * Asynchronously downloads {@code amount} of latest comments on the parent
     * with id {@code parentId} starting from {@code lastRetrieved} and passes list of
     * their models through {@code onSuccess} method of {@code callback}.
     * If download of the documents has failed, passes the error message through {@code onFailure}
     * method of the {@code callback}.
     * @param parentId id of the parent which comments to retrieve.
     * @param amount number of documents to retrieve.
     * @param callback Callback to pass list of models of the retrieved documents or
     *                 an error message.
     */
    public void downloadRecent(String parentId, int amount, ItemDownloadCallback<List<Comment>> callback) {
        Query basicQuery = collection.whereEqualTo(Comment.PARENT_ID, parentId);
        super.downloadRecentWithQuery(basicQuery, amount, callback);
    }

    /**
     * Converts a FireBase document snapshot of a comment collection into an instance
     * of comment model.
     * @param document FireBase document snapshot of a comment collection.
     * @return Instance of the comment model.
     */
    @Override
    public Comment deserialize(DocumentSnapshot document) {
        return new Comment(
                document.getId(),
                document.getString(Comment.PUBLISHER_ID),
                document.getString(Comment.CONTENT),
                document.getString(Comment.PARENT_ID),
                document.getTimestamp(Comment.TIMESTAMP).toDate());
    }

    /**
     * Converts an instance of the comment model to a corresponding map for uploading to a
     * comment collection.
     * @param comment Instance of the comment model.
     * @return Map for uploading to a comment collection.
     */
    @Override
    public Map<String, Object> serialize(Comment comment) {
        Map<String, Object> questionData = new HashMap<>();

        questionData.put(Comment.PUBLISHER_ID, comment.getPublisherId());
        questionData.put(Comment.CONTENT, comment.getText());
        questionData.put(Comment.PARENT_ID, comment.getParentId());
        questionData.put(Comment.TIMESTAMP, new Timestamp(comment.getDatetime()));

        return questionData;
    }
}
