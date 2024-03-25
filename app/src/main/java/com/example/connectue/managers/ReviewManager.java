package com.example.connectue.managers;

import com.example.connectue.model.Review;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReviewManager extends InteractableManager<Review> {

    /**
     * Constructor for a manager of reviews.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of the collection that stores posts in the database.
     * @param likeCollectionName Name of the collection that stores review likes in the database.
     * @param dislikeCollectionName Name of the collection that stores review dislikes in the database.
     * @param commentCollectionName Name of the collection that stores review comments in the database.
     */
    public ReviewManager(FirebaseFirestore db, String collectionName,
                       String likeCollectionName, String dislikeCollectionName,
                       String commentCollectionName) {
        super(db, collectionName, likeCollectionName, dislikeCollectionName, commentCollectionName);

        tag = "ReviewManager class: ";
    }


    /**
     * Converts a FireBase document snapshot of the review collection into an instance
     * of review model.
     * @param document FireBase document snapshot of the review collection.
     * @return Instance of the review model.
     */
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
                document.getTimestamp("timestamp").toDate(),
                document.getString("parentCourseId")
        );
    }

    /**
     * Converts an instance of the review model to a corresponding map for uploading to the
     * review collection.
     * @param review Instance of the review model.
     * @return Map for uploading to review collection.
     */
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
        reviewData.put("parentCourseId", review.getParentCourseId());

        return reviewData;
    }
}
