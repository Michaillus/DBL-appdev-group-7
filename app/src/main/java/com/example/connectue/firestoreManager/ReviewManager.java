package com.example.connectue.firestoreManager;

import com.example.connectue.model.Review;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReviewManager extends InteractableManager<Review> {

    public ReviewManager(FirebaseFirestore db, String collectionName,
                       String likeCollectionName, String dislikeCollectionName) {
        super(db, collectionName, likeCollectionName, dislikeCollectionName);
    }

    @Override
    protected Review deserialize(DocumentSnapshot document) {
        return new Review(
                document.getId(),
                document.getString("publisher"),
                document.getString("text"),
                document.getLong("stars"),
                document.getLong("likes"),
                document.getLong("dislikes"),
                document.getLong("comments"),
                document.getTimestamp("timestamp").toDate());
    }

    @Override
    protected Map<String, Object> serialize(Review review) {
        Map<String, Object> postData = new HashMap<>();


        postData.put("publisher", review.getPublisherId());
        postData.put("text", review.getText());
        postData.put("stars", review.getStars());
        postData.put("likes", review.getLikeNumber());
        postData.put("dislikes", review.getLikeNumber());
        postData.put("comments", review.getCommentNumber());
        postData.put("timestamp", new Timestamp(review.getDatetime()));

        return postData;
    }
}
