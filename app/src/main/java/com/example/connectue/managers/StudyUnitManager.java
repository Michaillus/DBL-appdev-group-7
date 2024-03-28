package com.example.connectue.managers;

import android.util.Log;

import com.example.connectue.interfaces.ItemUploadCallback;
import com.example.connectue.model.StudyUnit;
import com.example.connectue.model.CourseReview;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StudyUnitManager extends EntityManager<StudyUnit> {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "StudyUnitManager";

    /**
     * Type of the study unit that manager works with - course or major.
     */
    private StudyUnit.StudyUnitType type;

    /**
     * Constructor for study unit manager given instance of FireStore database and the name of
     * a study units collection in that database.
     *
     * @param db             Instance of a FireStore database.
     * @param collectionName Name of a study units collection in the database.
     */
    public StudyUnitManager(FirebaseFirestore db, String collectionName) {
        super(db, collectionName);
        if (collectionName.equals(StudyUnit.COURSE_COLLECTION_NAME)) {
            // Study unit is a course.
            this.type = StudyUnit.StudyUnitType.COURSE;
        } else {
            // Study unit is a major.
            this.type = StudyUnit.StudyUnitType.MAJOR;
        }
    }

    /**
     * Adds impact of the rating of the review towards the average rating of the study unit in the
     * database.
     * @param studyUnit A study unit.
     * @param studyUnitReview the review to given the study unit.
     * @param callback Callback that is called when the method is finished - successfully or
     *                 with an error.
     */
    public void addRating(StudyUnit studyUnit, CourseReview studyUnitReview, ItemUploadCallback callback) {

        studyUnit.setRatingSum(studyUnit.getRatingSum() + studyUnitReview.getStars());
        studyUnit.setRatingNumber(studyUnit.getRatingNumber() + 1);
        set(studyUnit, studyUnit.getId(), new ItemUploadCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while changing rating of a study unit");
                callback.onFailure(e);
            }
        });
    }

    /**
     * Converts a study unit document in the database into the instance of study unit model.
     * @param document FireBase document snapshot of a study unit.
     * @return Instance of the study unit corresponding to the document.
     */
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

    /**
     * Converts an instance of the study unit to the map for uploading to the database.
     * @param studyUnit Instance of the study unit model.
     * @return Map for uploading the study unit to the database.
     */
    @Override
    protected Map<String, Object> serialize(StudyUnit studyUnit) {
        Map<String, Object> majorData = new HashMap<>();

        majorData.put(StudyUnit.NAME_ATTRIBUTE, studyUnit.getName());
        majorData.put(StudyUnit.CODE_ATTRIBUTE, studyUnit.getCode());
        majorData.put(StudyUnit.RATING_SUM_ATTRIBUTE, studyUnit.getRatingSum());
        majorData.put(StudyUnit.RATING_NUMBER_ATTRIBUTE, studyUnit.getRatingNumber());


        return majorData;
    }
}
