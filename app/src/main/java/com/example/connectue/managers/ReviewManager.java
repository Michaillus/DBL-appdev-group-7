package com.example.connectue.managers;

import android.util.Log;

import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.interfaces.ItemExistsCallback;
import com.example.connectue.interfaces.ItemUploadCallback;
import com.example.connectue.model.StudyUnit;
import com.example.connectue.model.Review;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewManager extends InteractableManager<Review> {

    /**
     * Constructor for a manager of reviews of study units (study units or majors).
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of the collection that stores study units in the database.
     * @param likeCollectionName Name of the collection that stores review likes in the database.
     * @param dislikeCollectionName Name of the collection that stores review dislikes
     *                              in the database.
     * @param commentCollectionName Name of the collection that stores review comments
     *                              in the database.
     */
    public ReviewManager(FirebaseFirestore db, String collectionName,
                         String likeCollectionName, String dislikeCollectionName,
                         String commentCollectionName) {
        super(db, collectionName, likeCollectionName, dislikeCollectionName, commentCollectionName);

        tag = "StudyUnitReviewManager";
    }

    /**
     * Retrieves {@code amount} of latest reviews on the study unit starting from
     * {@code lastRetrieved}.
     * @param studyUnitId id of the study unit which reviews to retrieve.
     * @param amount number of reviews to retrieve.
     * @param callback Callback to pass list of retrieved reviews or an error message.
     */
    public void downloadRecent(String studyUnitId, int amount, ItemDownloadCallback<List<Review>> callback) {
        Query basicQuery = collection.whereEqualTo(Review.PARENT_ID_ATTRIBUTE, studyUnitId);
        super.downloadRecentWithQuery(basicQuery, amount, callback);
    }

    /**
     * Adds a study unit review to the database and changes the average rating of the study unit
     * respectively.
     * @param review Study unit review to upload.
     * @param callback Callback that is called when the upload of review is finished or an
     *                 error occurred.
     */
    public void addReview(Review review, StudyUnit studyUnit, ItemUploadCallback callback) {
        upload(review, new ItemUploadCallback() {
            @Override
            public void onSuccess() {
                StudyUnitManager studyUnitManager = new StudyUnitManager(FirebaseFirestore.getInstance(),
                        studyUnit.getStudyUnitCollectionName());
                studyUnitManager.addRating(studyUnit, review, new ItemUploadCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(tag, "Error while updating studyUnit rating");
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(tag, "Error while uploading studyUnit review");
                callback.onFailure(e);
            }
        });
    }

    /**
     * Checks and returns through the callback if the user have a review on the study unit.
     * @param studyUnitId Study unit to check.
     * @param userId User to check.
     * @param callback Returns if the user has a review on the study unit through the {@code onSuccess}
     *                 method, if database request is successful. Otherwise, returns exception
     *                 through the {@code onFailure} method.
     */
    public void hasUserReviewedStudyUnit(String studyUnitId, String userId, ItemExistsCallback callback) {
        Query query = collection.whereEqualTo(Review.PARENT_ID_ATTRIBUTE, studyUnitId)
                .whereEqualTo(Review.PUBLISHER_ID_ATTRIBUTE, userId);
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                // Returns if there exists a review on the study unit from the user.
                callback.onSuccess(snapshot.getCount() > 0);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Converts a FireBase document snapshot of the study unit review collection into an instance
     * of study unit review model.
     * @param document FireBase document snapshot of the study unit review collection.
     * @return Instance of the study unit review model.
     */
    @Override
    public Review deserialize(DocumentSnapshot document) {
        return new Review(
                document.getId(),
                document.getString(Review.PUBLISHER_ID_ATTRIBUTE),
                document.getString(Review.TEXT_ATTRIBUTE),
                document.getLong(Review.STARS_ATTRIBUTE),
                document.getLong(Review.LIKE_NUMBER_ATTRIBUTE),
                document.getLong(Review.DISLIKE_NUMBER_ATTRIBUTE),
                document.getLong(Review.COMMENT_NUMBER_ATTRIBUTE),
                document.getTimestamp(Review.DATETIME_ATTRIBUTE).toDate(),
                document.getString(Review.PARENT_ID_ATTRIBUTE)
        );
    }

    /**
     * Converts an instance of the study unit review model to a corresponding map for uploading to the
     * study unit review collection.
     * @param review Instance of the study unit review model.
     * @return Map for uploading to study unit review collection.
     */
    @Override
    public Map<String, Object> serialize(Review review) {
        Map<String, Object> reviewData = new HashMap<>();


        reviewData.put(Review.PUBLISHER_ID_ATTRIBUTE, review.getPublisherId());
        reviewData.put(Review.TEXT_ATTRIBUTE, review.getText());
        reviewData.put(Review.STARS_ATTRIBUTE, review.getStars());
        reviewData.put(Review.LIKE_NUMBER_ATTRIBUTE, review.getLikeNumber());
        reviewData.put(Review.DISLIKE_NUMBER_ATTRIBUTE, review.getLikeNumber());
        reviewData.put(Review.COMMENT_NUMBER_ATTRIBUTE, review.getCommentNumber());
        reviewData.put(Review.DATETIME_ATTRIBUTE, new Timestamp(review.getDatetime()));
        reviewData.put(Review.PARENT_ID_ATTRIBUTE, review.getParentId());

        return reviewData;
    }
}
