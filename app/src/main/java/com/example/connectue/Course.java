package com.example.connectue;

import java.util.ArrayList;
import java.util.List;

public class Course {
    public String courseName;
    private String courseId;
    public String courseCode;

    public List<Integer> rating;

    public Course(String courseName, String courseCode, String courseId) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.rating = new ArrayList<>();
        this.courseId = courseId;
    }

    /**
     * Add a rating value to the list of ratings related to a course.
     * @param rating the rating to add.
     * @pre {@code rating >= 0 and rating <= 5}
     */
    public void addRating(int rating) {
        this.rating.add(rating);
    }

    /**
     * Returns the name of the course.
     * @return courseName.
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Returns the id of the course.
     * @return courseId.
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * Returns the code of the course.
     * @return courseCode;
     */
    public String getCourseCode() {
        return courseCode;
    }
}
