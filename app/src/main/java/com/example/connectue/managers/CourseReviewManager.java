package com.example.connectue.managers;

import android.util.Log;

import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.interfaces.ItemExistsCallback;
import com.example.connectue.interfaces.ItemUploadCallback;
import com.example.connectue.model.Course;
import com.example.connectue.model.CourseReview;
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

public class CourseReviewManager extends InteractableManager<CourseReview> {

    /**
     * Constructor for a manager of reviews of courses.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of the collection that stores posts in the database.
     * @param likeCollectionName Name of the collection that stores review likes in the database.
     * @param dislikeCollectionName Name of the collection that stores review dislikes in the database.
     * @param commentCollectionName Name of the collection that stores review comments in the database.
     */
    public CourseReviewManager(FirebaseFirestore db, String collectionName,
                               String likeCollectionName, String dislikeCollectionName,
                               String commentCollectionName) {
        super(db, collectionName, likeCollectionName, dislikeCollectionName, commentCollectionName);

        tag = "CourseReviewManager";
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
    public void downloadRecent(String courseId, int amount, ItemDownloadCallback<List<CourseReview>> callback) {
        Query basicQuery = collection.whereEqualTo(CourseReview.PARENT_COURSE_ID_ATTRIBUTE, courseId);
        super.downloadRecentWithQuery(basicQuery, amount, callback);
    }

    /**
     * Adds a course review to the database and changes the average rating of the course
     * respectively.
     * @param courseReview Course review to upload.
     * @param callback Callback that is called when the upload of review is finished or an
     *                 error occurred.
     */
    public void addReview(CourseReview courseReview, Course course, ItemUploadCallback callback) {
        upload(courseReview, new ItemUploadCallback() {
            @Override
            public void onSuccess() {
                CourseManager courseManager = new CourseManager(FirebaseFirestore.getInstance(),
                        course.getStudyUnitCollectionName());
                courseManager.addRating(course, courseReview, new ItemUploadCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(tag, "Error while updating course rating");
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(tag, "Error while uploading course review");
                callback.onFailure(e);
            }
        });
    }

    /**
     * Checks and returns through the callback if the user have a review on the course.
     * @param courseId Course to check.
     * @param userId User to check.
     * @param callback Returns if the user has a review on the course through the {@code onSuccess}
     *                 method, if database request is successful. Otherwise, returns exception
     *                 through the {@code onFailure} method.
     */
    public void hasUserReviewedCourse(String courseId, String userId, ItemExistsCallback callback) {
        Query query = collection.whereEqualTo(CourseReview.PARENT_COURSE_ID_ATTRIBUTE, courseId)
                .whereEqualTo(CourseReview.PUBLISHER_ID_ATTRIBUTE, userId);
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                // Returns if there exists a review on the course from the user.
                callback.onSuccess(snapshot.getCount() > 0);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Converts a FireBase document snapshot of the course review collection into an instance
     * of course review model.
     * @param document FireBase document snapshot of the course review collection.
     * @return Instance of the course review model.
     */
    @Override
    protected CourseReview deserialize(DocumentSnapshot document) {
        return new CourseReview(
                document.getId(),
                document.getString(CourseReview.PUBLISHER_ID_ATTRIBUTE),
                document.getString(CourseReview.TEXT_ATTRIBUTE),
                document.getLong(CourseReview.STARS_ATTRIBUTE),
                document.getLong(CourseReview.LIKE_NUMBER_ATTRIBUTE),
                document.getLong(CourseReview.DISLIKE_NUMBER_ATTRIBUTE),
                document.getLong(CourseReview.COMMENT_NUMBER_ATTRIBUTE),
                document.getTimestamp(CourseReview.DATETIME_ATTRIBUTE).toDate(),
                document.getString(CourseReview.PARENT_COURSE_ID_ATTRIBUTE)
        );
    }

    /**
     * Converts an instance of the course review model to a corresponding map for uploading to the
     * course review collection.
     * @param courseReview Instance of the course review model.
     * @return Map for uploading to course review collection.
     */
    @Override
    protected Map<String, Object> serialize(CourseReview courseReview) {
        Map<String, Object> reviewData = new HashMap<>();


        reviewData.put(CourseReview.PUBLISHER_ID_ATTRIBUTE, courseReview.getPublisherId());
        reviewData.put(CourseReview.TEXT_ATTRIBUTE, courseReview.getText());
        reviewData.put(CourseReview.STARS_ATTRIBUTE, courseReview.getStars());
        reviewData.put(CourseReview.LIKE_NUMBER_ATTRIBUTE, courseReview.getLikeNumber());
        reviewData.put(CourseReview.DISLIKE_NUMBER_ATTRIBUTE, courseReview.getLikeNumber());
        reviewData.put(CourseReview.COMMENT_NUMBER_ATTRIBUTE, courseReview.getCommentNumber());
        reviewData.put(CourseReview.DATETIME_ATTRIBUTE, new Timestamp(courseReview.getDatetime()));
        reviewData.put(CourseReview.PARENT_COURSE_ID_ATTRIBUTE, courseReview.getParentCourseId());

        return reviewData;
    }
}
