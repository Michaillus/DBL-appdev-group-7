package com.example.connectue.firestoreManager;

import com.example.connectue.model.Post;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostManager extends InteractableManager<Post> {

    public PostManager(FirebaseFirestore db, String collectionName,
                       String likeCollectionName, String dislikeCollectionName) {
        super(db, collectionName, likeCollectionName, dislikeCollectionName);
    }

    @Override
    protected Post deserialize(DocumentSnapshot document) {
        return new Post(
                document.getId(),
                document.getString("publisher"),
                document.getString("text"),
                document.getString("photoURL"),
                document.getLong("likes"),
                document.getLong("comments"),
                document.getTimestamp("timestamp").toDate());
    }

    @Override
    protected Map<String, Object> serialize(Post post) {
        Map<String, Object> postData = new HashMap<>();

        postData.put("text", post.getText());
        postData.put("photoURL", post.getImageUrl());
        postData.put("likes", post.getLikeNumber());
        postData.put("comments", post.getCommentNumber());
        postData.put("publisher", post.getPublisherId());
        postData.put("likedByUsers", new ArrayList<String>());
        postData.put("timestamp", new Timestamp(post.getDatetime()));

        return postData;
    }
}
