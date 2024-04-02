package com.example.connectue.managers;

import android.util.Log;

import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.model.Comment;
import com.example.connectue.model.Reply;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplyManager extends EntityManager<Reply> {

    private ReplyManager replyManager;
    /**
     * Constructor for entity manager given instance of  FireStore database and the name of
     * collection in that database.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of collection in the database {@code db}.
     */
    protected ReplyManager(FirebaseFirestore db, String collectionName) {
        super(db, collectionName);

        tag = "ReplyManager";
    }




    public void downloadRecent(String parentId, int amount, ItemDownloadCallback<List<Reply>> callback) {
        Query basicQuery = collection.whereEqualTo(Reply.PARENT_ID, parentId);
        super.downloadRecentWithQuery(basicQuery, amount, callback);
    }




    @Override
    public Reply deserialize(DocumentSnapshot document) {
        return new Reply(
                document.getId(),
                document.getString(Reply.PUBLISHER_ID),
                document.getString(Reply.CONTENT),
                document.getString(Reply.PARENT_ID),
                document.getTimestamp(Reply.TIMESTAMP).toDate());
    }

    @Override
    public Map<String, Object> serialize(Reply reply) {
        Map<String, Object> replyData = new HashMap<>();

        replyData.put(Reply.PUBLISHER_ID, reply.getPublisherId());
        replyData.put(Reply.CONTENT, reply.getText());
        replyData.put(Reply.PARENT_ID, reply.getParentId());
        replyData.put(Reply.TIMESTAMP, new Timestamp(reply.getTimestamp()));

        return replyData;
    }
}
