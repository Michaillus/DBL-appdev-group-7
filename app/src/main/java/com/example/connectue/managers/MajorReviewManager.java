package com.example.connectue.managers;

import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.interfaces.ItemExistsCallback;
import com.example.connectue.model.MajorReview;
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

public class MajorReviewManager extends InteractableManager<MajorReview> {

    /**
     * Constructor for a manager of reviews of majors.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of the collection that stores posts in the database.
     * @param likeCollectionName Name of the collection that stores review likes in the database.
     * @param dislikeCollectionName Name of the collection that stores review dislikes in the database.
     * @param commentCollectionName Name of the collection that stores review comments in the database.
     */
    public MajorReviewManager(FirebaseFirestore db, String collectionName,
                               String likeCollectionName, String dislikeCollectionName,
                               String commentCollectionName) {
        super(db, collectionName, likeCollectionName, dislikeCollectionName, commentCollectionName);

        tag = "MajorReviewManager";
    }

    /**
     * Retrieves {@code amount} of latest reviews on the {@code majorId} major
     * starting from {@code lastRetrieved} and passes list of their models through
     * {@code onSuccess} method of {@code callback}.
     * If download of reviews has failed, passes the error message through {@code onFailure}
     * method of the {@code callback}.
     * @param majorId id of the major which reviews to retrieve.
     * @param amount number of reviews to retrieve.
     * @param callback Callback to pass list of retrieved reviews or an error message.
     */
    public void downloadRecent(String majorId, int amount, ItemDownloadCallback<List<MajorReview>> callback) {
        Query basicQuery = collection.whereEqualTo(MajorReview.PARENT_MAJOR_ID_ATTRIBUTE, majorId);
        super.downloadRecentWithQuery(basicQuery, amount, callback);
    }

    /**
     * Checks and returns through the callback if the user have a review on the major.
     * @param majorId Major to check.
     * @param userId User to check.
     * @param callback Returns if the user has a review on the major through the {@code onSuccess}
     *                 method, if database request is successful. Otherwise, returns exception
     *                 through the {@code onFailure} method.
     */
    public void hasUserReviewedMajor(String majorId, String userId, ItemExistsCallback callback) {
        Query query = collection.whereEqualTo(MajorReview.PARENT_MAJOR_ID_ATTRIBUTE, majorId)
                .whereEqualTo(MajorReview.PUBLISHER_ID_ATTRIBUTE, userId);
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                // Returns if there exists a review on the major from the user.
                callback.onSuccess(snapshot.getCount() > 0);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Converts a FireBase document snapshot of the major review collection into an instance
     * of major review model.
     * @param document FireBase document snapshot of the major review collection.
     * @return Instance of the major review model.
     */
    @Override
    protected MajorReview deserialize(DocumentSnapshot document) {
        return new MajorReview(
                document.getId(),
                document.getString(MajorReview.PUBLISHER_ID_ATTRIBUTE),
                document.getString(MajorReview.TEXT_ATTRIBUTE),
                document.getLong(MajorReview.STARS_ATTRIBUTE),
                document.getLong(MajorReview.LIKE_NUMBER_ATTRIBUTE),
                document.getLong(MajorReview.DISLIKE_NUMBER_ATTRIBUTE),
                document.getLong(MajorReview.COMMENT_NUMBER_ATTRIBUTE),
                document.getTimestamp(MajorReview.DATETIME_ATTRIBUTE).toDate(),
                document.getString(MajorReview.PARENT_MAJOR_ID_ATTRIBUTE)
        );
    }

    /**
     * Converts an instance of the major review model to a corresponding map for uploading to the
     * major review collection.
     * @param majorReview Instance of the major review model.
     * @return Map for uploading to major review collection.
     */
    @Override
    protected Map<String, Object> serialize(MajorReview majorReview) {
        Map<String, Object> reviewData = new HashMap<>();


        reviewData.put(MajorReview.PUBLISHER_ID_ATTRIBUTE, majorReview.getPublisherId());
        reviewData.put(MajorReview.TEXT_ATTRIBUTE, majorReview.getText());
        reviewData.put(MajorReview.STARS_ATTRIBUTE, majorReview.getStars());
        reviewData.put(MajorReview.LIKE_NUMBER_ATTRIBUTE, majorReview.getLikeNumber());
        reviewData.put(MajorReview.DISLIKE_NUMBER_ATTRIBUTE, majorReview.getLikeNumber());
        reviewData.put(MajorReview.COMMENT_NUMBER_ATTRIBUTE, majorReview.getCommentNumber());
        reviewData.put(MajorReview.DATETIME_ATTRIBUTE, new Timestamp(majorReview.getDatetime()));
        reviewData.put(MajorReview.PARENT_MAJOR_ID_ATTRIBUTE, majorReview.getParentMajorId());

        return reviewData;
    }
}
