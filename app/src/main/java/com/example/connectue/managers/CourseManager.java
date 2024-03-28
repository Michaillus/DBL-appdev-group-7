package com.example.connectue.managers;

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

    /**
     * Constructor for course manager given instance of  FireStore database and the name of
     * courses collection in that database.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of majors collection in the database.
     */
    public CourseManager(FirebaseFirestore db, String collectionName) {
        super(db, collectionName);
    }

    public void addRating(CourseReview courseReview, ItemUploadCallback callback) {
        downloadOne(courseReview.getParentCourseId(), new ItemDownloadCallback<Course>() {
            @Override
            public void onSuccess(Course course) {

                course.setRatingSum(course.getRatingSum() + courseReview.getStars());
                course.setRatingNumber(course.getRatingNumber() + 1);
                set(course, course.getId(), new ItemUploadCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    @Override
    protected Course deserialize(DocumentSnapshot document) {
        return new Course(
                document.getId(),
                document.getString(Course.COURSE_NAME_ATTRIBUTE),
                document.getString(Course.COURSE_CODE_ATTRIBUTE),
                document.getLong(Course.RATING_SUM_ATTRIBUTE).longValue(),
                document.getLong(Course.RATING_NUMBER_ATTRIBUTE).longValue());
    }

    @Override
    protected Map<String, Object> serialize(Course course) {
        Map<String, Object> majorData = new HashMap<>();

        majorData.put(Course.COURSE_NAME_ATTRIBUTE, course.getCourseName());
        majorData.put(Course.COURSE_CODE_ATTRIBUTE, course.getCourseCode());
        majorData.put(Course.RATING_SUM_ATTRIBUTE, course.getRatingSum());
        majorData.put(Course.RATING_NUMBER_ATTRIBUTE, course.getRatingNumber());


        return majorData;
    }
}
