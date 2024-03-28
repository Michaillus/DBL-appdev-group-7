package com.example.connectue.model;

import java.util.ArrayList;
import java.util.List;

public class Course {

    /**
     * Name of courses collection in the database.
     */
    public static final String COURSE_COLLECTION_NAME = "courses";

    /**
     * Name of course name attribute in the majors collection.
     */
    public static final String COURSE_NAME_ATTRIBUTE = "courseName";

    /**
     * Name of course code attribute in the majors collection.
     */
    public static final String COURSE_CODE_ATTRIBUTE = "courseCode";

    /**
     * Name of course rating sum attribute in the majors collection.
     */
    public static final String COURSE_RATING_SUM_ATTRIBUTE = "ratingSum";

    /**
     * Name of course rating number attribute in the majors collection.
     */
    public static final String COURSE_RATING_NUMBER_ATTRIBUTE = "ratingNumber";

    private String courseId;

    private String courseName;

    private String courseCode;

    /**
     * Sum of stars over all ratings of the course.
     */
    private long ratingSum;

    /**
     * Number of ratings on the course.
     */
    private long ratingNumber;

    public Course(String courseId, String courseName, String courseCode, Long ratingSum,
                  Long ratingNumber) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courseId = courseId;
        this.ratingSum = ratingSum;
        this.ratingNumber = ratingNumber;
    }

    /**
     * Returns the id of the course.
     * @return courseId.
     */
    public String getId() {
        return courseId;
    }

    /**
     * Returns the name of the course.
     * @return courseName.
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Returns the code of the course.
     * @return courseCode;
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * Getter for sum of ratings of the course.
     * @return Sum of ratings.
     */
    public long getRatingSum() {
        return ratingSum;
    }

    /**
     * Setter for sum of ratings of the course.
     * @ratingSum Sum of ratings.
     */
    public void setRatingSum(long ratingSum) {
        this.ratingSum = ratingSum;
    }

    /**
     * Getter for number of ratings of the course.
     * @return Number of ratings.
     */
    public long getRatingNumber() {
        return ratingNumber;
    }

    /**
     * Setter for number of ratings of the course.
     * @param ratingNumber Number of ratings.
     */
    public void setRatingNumber(long ratingNumber) {
        this.ratingNumber = ratingNumber;
    }

    /**
     * Calculates average rating of the course.
     * @return Average rating of the course.
     */
    public Float getAverageRating() {
        if (ratingNumber == 0) {
            return (float) 0;
        } else {
            return (float) ratingSum / (float) ratingNumber;
        }
    }

    /**
     * Converts course model to a string.
     * @return String representing the course.
     */
    public String courseToString() {
        return courseId + "#" + courseName + "#" + courseCode + "#" + ratingSum + "#"
                + ratingNumber;
    }

    /**
     * Converts string representing a course to the course model.
     * @param string String representing a course.
     * @return Course model.
     */
    public static Course stringToCourse(String string) {
        String[] parts = string.split("#");
        return new Course(parts[0], parts[1], parts[2], Long.parseLong(parts[3]),
                Long.parseLong(parts[4]));
    }
}
