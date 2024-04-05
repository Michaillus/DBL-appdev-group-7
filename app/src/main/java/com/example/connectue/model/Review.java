package com.example.connectue.model;

import java.util.Date;

/**
 * Class that defines the model for a review of a course.
 */
public class Review extends Interactable{

    /**
     * Name of stars field in the reviews collection
     */
    public static final String STARS_ATTRIBUTE = "stars";

    /**
     * Name of parent course id field in the reviews collection
     */
    public static final String PARENT_ID_ATTRIBUTE = "parentId";
    
    /**
     * Rating in terms of number of stars from 1 to 5, that was given in the review.
     */
    protected long stars;

    /**
     * Id of the study unit for which review is written.
     */
    protected String parentId;

    /**
     * Constructor for a review that is not yet in the database.
     * @param publisherId Id of the publisher.
     * @param text Text of the review.
     * @param stars Rating stars of the review.
     * @param parentId Id of parent interactable.
     */
    public Review(String publisherId, String text, Long stars, String parentId) {
        super(publisherId, text);
        this.setStars(stars);
        this.setParentId(parentId);

        // Setting class tag for logs.
        tag = "Review model";
    }

    /**
     * Constructor for a review that is already in the database.
     * @param reviewId Id of the review.
     * @param publisherId Id of the publisher.
     * @param text Text of the review.
     * @param stars Rating stars of the review.
     * @param likeNumber Number of likes got of the review.
     * @param dislikeNumber Number of dislikes got of the review.
     * @param commentNumber Number of comments(unused) of the review.
     * @param datetime Date and time of creation.
     * @param parentId Id of parent interactable.
     * @throws IllegalArgumentException Thrown if any of the parameters are null.
     */
    public Review(String reviewId, String publisherId, String text, Long stars,
                  Long likeNumber, Long dislikeNumber, Long commentNumber, Date datetime, String parentId)
            throws IllegalArgumentException {

        super(reviewId, publisherId, text, likeNumber, dislikeNumber, commentNumber, datetime);
        this.parentId = parentId;
        this.setStars(stars);

        // Setting class tag for logs.
        tag = "Review model";

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
     * Getters and setters for parent study unit id.
     */
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
