package com.example.connectue.managers;

import com.example.connectue.model.Course;
import com.example.connectue.model.Major;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CourseManager extends EntityManager<Course> {

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

    @Override
    protected Course deserialize(DocumentSnapshot document) {
        return new Course(
                document.getId(),
                document.getString(Course.COURSE_NAME_ATTRIBUTE),
                document.getString(Course.COURSE_CODE_ATTRIBUTE));
    }

    @Override
    protected Map<String, Object> serialize(Course course) {
        Map<String, Object> majorData = new HashMap<>();

        majorData.put(Course.COURSE_NAME_ATTRIBUTE, course.getCourseName());
        majorData.put(Course.COURSE_CODE_ATTRIBUTE, course.getCourseCode());

        return majorData;
    }
}
