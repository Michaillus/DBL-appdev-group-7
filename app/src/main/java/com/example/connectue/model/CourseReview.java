package com.example.connectue.model;

import java.util.Date;

/**
 * Class that defines the model for a review of a course.
 */
public class CourseReview extends Interactable{

    /**
     * Name of stars field in the course reviews collection
     */
    public static final String STARS_ATTRIBUTE = "stars";

    /**
     * Name of parent course id field in the course reviews collection
     */
    public static final String PARENT_COURSE_ID_ATTRIBUTE = "parentId";
    
    /**
     * Rating in terms of number of stars from 1 to 5, that was given in the review.
     */
    protected long stars;

    /**
     * Id of the course for which review is written.
     */
    protected String parentCourseId;

    public CourseReview(String publisherId, String text, Long stars, String parentCourseId) {
        super(publisherId, text);

        this.setStars(stars);
        this.setParentCourseId(parentCourseId);

        // Setting class tag for logs.
        tag = "CourseReview";
    }

    public CourseReview(String reviewId, String publisherId, String text, Long stars,
                        Long likeNumber, Long dislikeNumber, Long commentNumber, Date datetime, String parentCourseId)
            throws IllegalArgumentException {

        super(reviewId, publisherId, text, likeNumber, dislikeNumber, commentNumber, datetime);
        this.parentCourseId = parentCourseId;
        this.setStars(stars);

        // Setting class tag for logs.
        tag = "CourseReview";

    }

    /**
     * Getters and setters for the stars.
     */
    public long getStars() {
        return stars;
    }

    public void setStars(long stars) {
        this.stars = stars;
    }

    /**
     * Getters and setters for parent course id.
     */
    public String getParentCourseId() {
        return parentCourseId;
    }

    public void setParentCourseId(String parentCourseId) {
        this.parentCourseId = parentCourseId;
    }
}
