package com.example.connectue.managers;

import android.util.Log;

import com.example.connectue.interfaces.ItemUploadCallback;
import com.example.connectue.model.StudyUnit;
import com.example.connectue.model.CourseReview;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CourseManager extends EntityManager<StudyUnit> {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "CourseManager";

    private StudyUnit.StudyUnitType type;

    /**
     * Constructor for course manager given instance of  FireStore database and the name of
     * courses collection in that database.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of majors collection in the database.
     */
    public CourseManager(FirebaseFirestore db, String collectionName) {
        super(db, collectionName);
        if (collectionName.equals(StudyUnit.COURSE_COLLECTION_NAME)) {
            this.type = StudyUnit.StudyUnitType.COURSE;
        } else {
            this.type = StudyUnit.StudyUnitType.MAJOR;
        }
    }

    public void addRating(StudyUnit course, CourseReview courseReview, ItemUploadCallback callback) {

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
    protected StudyUnit deserialize(DocumentSnapshot document) {

        return new StudyUnit(
                document.getId(),
                document.getString(StudyUnit.NAME_ATTRIBUTE),
                document.getString(StudyUnit.CODE_ATTRIBUTE),
                document.getLong(StudyUnit.RATING_SUM_ATTRIBUTE).longValue(),
                document.getLong(StudyUnit.RATING_NUMBER_ATTRIBUTE).longValue(),
                type);
    }

    @Override
    protected Map<String, Object> serialize(StudyUnit course) {
        Map<String, Object> majorData = new HashMap<>();

        majorData.put(StudyUnit.NAME_ATTRIBUTE, course.getName());
        majorData.put(StudyUnit.CODE_ATTRIBUTE, course.getCode());
        majorData.put(StudyUnit.RATING_SUM_ATTRIBUTE, course.getRatingSum());
        majorData.put(StudyUnit.RATING_NUMBER_ATTRIBUTE, course.getRatingNumber());


        return majorData;
    }
}
