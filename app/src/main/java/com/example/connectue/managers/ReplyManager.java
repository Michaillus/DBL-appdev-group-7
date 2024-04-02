package com.example.connectue.managers;

import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.model.Comment;
import com.example.connectue.model.Reply;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Map;

public class ReplyManager extends EntityManager<Reply> {
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
        Query basicQuery = collection.whereEqualTo(Comment.PARENT_ID, parentId);
        super.downloadRecentWithQuery(basicQuery, amount, callback);
    }




    @Override
    public Reply deserialize(DocumentSnapshot document) {
        return null;
    }

    @Override
    public Map<String, Object> serialize(Reply object) {
        return null;
    }
}
