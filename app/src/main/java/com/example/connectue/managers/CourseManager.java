package com.example.connectue.managers;

import android.util.Log;

import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.interfaces.ItemUploadCallback;
import com.example.connectue.model.Course;
import com.example.connectue.model.CourseReview;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CourseManager extends EntityManager<Course> {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "CourseManager";

    private Course.StudyUnitType type;

    /**
     * Constructor for course manager given instance of  FireStore database and the name of
     * courses collection in that database.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of majors collection in the database.
     */
    public CourseManager(FirebaseFirestore db, String collectionName) {
        super(db, collectionName);
        if (collectionName.equals(Course.COURSE_COLLECTION_NAME)) {
            this.type = Course.StudyUnitType.COURSE;
        } else {
            this.type = Course.StudyUnitType.MAJOR;
        }
    }

    public void addRating(Course course, CourseReview courseReview, ItemUploadCallback callback) {

        course.setRatingSum(course.getRatingSum() + courseReview.getStars());
        course.setRatingNumber(course.getRatingNumber() + 1);
        set(course, course.getId(), new ItemUploadCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while changing rating of a course");
                callback.onFailure(e);
            }
        });
    }

    @Override
    protected Course deserialize(DocumentSnapshot document) {

        return new Course(
                document.getId(),
                document.getString(Course.NAME_ATTRIBUTE),
                document.getString(Course.CODE_ATTRIBUTE),
                document.getLong(Course.RATING_SUM_ATTRIBUTE).longValue(),
                document.getLong(Course.RATING_NUMBER_ATTRIBUTE).longValue(),
                type);
    }

    @Override
    protected Map<String, Object> serialize(Course course) {
        Map<String, Object> majorData = new HashMap<>();

        majorData.put(Course.NAME_ATTRIBUTE, course.getName());
        majorData.put(Course.CODE_ATTRIBUTE, course.getCode());
        majorData.put(Course.RATING_SUM_ATTRIBUTE, course.getRatingSum());
        majorData.put(Course.RATING_NUMBER_ATTRIBUTE, course.getRatingNumber());


        return majorData;
    }
}
