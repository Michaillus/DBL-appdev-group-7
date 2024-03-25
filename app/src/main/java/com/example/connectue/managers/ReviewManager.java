package com.example.connectue.managers;

import com.example.connectue.model.Review;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReviewManager extends InteractableManager<Review> {

    /**
     * Constructor for manager of reviews.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of collection that stores posts in the database.
     * @param likeCollectionName Name of collection that stores review likes in the database.
     * @param dislikeCollectionName Name of collection that stores review dislikes in the database.
     * @param commentCollectionName Name of collection that stores review comments in the database.
     */
    public ReviewManager(FirebaseFirestore db, String collectionName,
                       String likeCollectionName, String dislikeCollectionName,
                       String commentCollectionName) {
        super(db, collectionName, likeCollectionName, dislikeCollectionName, commentCollectionName);

        TAG = "ReviewManager class: ";
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
        Map<String, Object> reviewData = new HashMap<>();


        reviewData.put("publisher", review.getPublisherId());
        reviewData.put("text", review.getText());
        reviewData.put("stars", review.getStars());
        reviewData.put("likes", review.getLikeNumber());
        reviewData.put("dislikes", review.getLikeNumber());
        reviewData.put("comments", review.getCommentNumber());
        reviewData.put("timestamp", new Timestamp(review.getDatetime()));

        return reviewData;
    }
}
