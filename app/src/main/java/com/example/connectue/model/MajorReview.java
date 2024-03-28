//package com.example.connectue.model;
//
//import java.util.Date;
//
///**
// * Class that defines the model for a review of a major.
// */
//public class MajorReview extends Interactable{
//
//    /**
//     * Name of review collection in the database.
//     */
//    public static final String MAJOR_REVIEW_COLLECTION_NAME = "majorReviews";
//
//    /**
//     * Name of review likes collection in the database.
//     */
//    public static final String MAJOR_REVIEW_LIKE_COLLECTION_NAME = "majorReviews-likes";
//
//    /**
//     * Name of review dislikes collection in the database.
//     */
//    public static final String MAJOR_REVIEW_DISLIKE_COLLECTION_NAME = "majorReviews-dislikes";
//
//    /**
//     * Name of review comments collection in the database.
//     */
//    public static final String MAJOR_REVIEW_COMMENT_COLLECTION_NAME = "majorReviews-comments";
//
//    /**
//     * Name of stars field in the major reviews collection
//     */
//    public static final String STARS_ATTRIBUTE = "stars";
//
//    /**
//     * Name of parent major id field in the major reviews collection
//     */
//    public static final String PARENT_MAJOR_ID_ATTRIBUTE = "parentMajorId";
//
//    /**
//     * Rating in terms of number of stars from 1 to 5, that was given in the review.
//     */
//    protected long stars;
//
//    /**
//     * Id of the major for which review is written.
//     */
//    protected String parentMajorId;
//
//    public MajorReview(String publisherId, String text, Long stars, String parentMajorId) {
//        super(publisherId, text);
//
//        this.setStars(stars);
//        this.setParentMajorId(parentMajorId);
//
//        // Setting class tag for logs.
//        tag = "Review Model";
//    }
//
//    public MajorReview(String reviewId, String publisherId, String text, Long stars,
//                       Long likeNumber, Long dislikeNumber, Long commentNumber, Date datetime, String parentMajorId)
//            throws IllegalArgumentException {
//
//        super(reviewId, publisherId, text, likeNumber, dislikeNumber, commentNumber, datetime);
//        this.parentMajorId = parentMajorId;
//        this.setStars(stars);
//
//        // Setting class tag for logs.
//        tag = "MajorReview";
//
//    }
//
//    /**
//     * Getters and setters for the stars.
//     */
//    public long getStars() {
//        return stars;
//    }
//
//    public void setStars(long stars) {
//        this.stars = stars;
//    }
//
//    /**
//     * Getters and setters for parent major id.
//     */
//    public String getParentMajorId() {
//        return parentMajorId;
//    }
//
//    public void setParentMajorId(String parentMajorId) {
//        this.parentMajorId = parentMajorId;
//    }
//}
