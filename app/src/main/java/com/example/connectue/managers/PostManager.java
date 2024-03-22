package com.example.connectue.managers;

import android.util.Log;

import com.example.connectue.model.Post;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PostManager extends InteractableManager<Post> {

    /**
     * Constructor for manager of posts.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of collection that stores posts in the database.
     * @param likeCollectionName Name of collection that stores post likes in the database.
     * @param dislikeCollectionName Name of collection that stores post dislikes in the database.
     * @param commentCollectionName Name of collection that stores post comments in the database.
     */
    public PostManager(FirebaseFirestore db, String collectionName,
                       String likeCollectionName, String dislikeCollectionName,
                       String commentCollectionName) {
        super(db, collectionName, likeCollectionName, dislikeCollectionName, commentCollectionName);

        TAG = "PostManager class: ";
    }

    @Override
    protected Post deserialize(DocumentSnapshot document) {
        // TODO: implement dislikes for posts.
        Log.i(TAG, "TODO: implement dislikes for posts");
        return new Post(
                document.getId(),
                document.getString("publisher"),
                document.getString("text"),
                document.getString("photoURL"),
                document.getLong("likes"),
                0L,
                document.getLong("comments"),
                document.getTimestamp("timestamp").toDate());
    }

    @Override
    protected Map<String, Object> serialize(Post post) {
        Map<String, Object> postData = new HashMap<>();

        postData.put("text", post.getText());
        postData.put("photoURL", post.getImageUrl());
        postData.put("likes", post.getLikeNumber());
        postData.put("dislikes", post.getDislikeNumber());
        postData.put("comments", post.getCommentNumber());
        postData.put("publisher", post.getPublisherId());
        postData.put("timestamp", new Timestamp(post.getDatetime()));

        return postData;
    }
}