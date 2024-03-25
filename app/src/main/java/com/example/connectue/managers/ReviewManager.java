package com.example.connectue.managers;

import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.model.Comment;
import com.example.connectue.model.Review;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
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
     * Retrieves {@code amount} of latest reviews on the {@code courseId} course
     * starting from {@code lastRetrieved} and passes list of their models through
     * {@code onSuccess} method of {@code callback}.
     * If download of reviews has failed, passes the error message through {@code onFailure}
     * method of the {@code callback}.
     * @param courseId id of the course which reviews to retrieve.
     * @param amount number of reviews to retrieve.
     * @param callback Callback to pass list of retrieved reviews or an error message.
     */
    public void downloadRecent(String courseId, int amount, FireStoreDownloadCallback<List<Review>> callback) {
        Query basicQuery = collection.whereEqualTo("parentCourseId", courseId);
        super.downloadRecentWithQuery(basicQuery, amount, callback);
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
