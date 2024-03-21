package com.example.connectue.model;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class Review extends Interactable{

    /**
     * Class tag for logs.
     */
    private static final String TAG = "Review class: ";

    
    /**
     * Name of review collection in the database.
     */
    public static final String REVIEW_COLLECTION_NAME = "reviews";

    /**
     * Name of review likes collection in the database.
     */
    public static final String REVIEW_LIKE_COLLECTION_NAME = "review-likes";

    /**
     * Name of review dislikes collection in the database.
     */
    public static final String REVIEW_DISLIKE_COLLECTION_NAME = "review-dislikes";

    /**
     * Name of review comments collection in the database.
     */
    public static final String REVIEW_COMMENT_COLLECTION_NAME = "review-comments";

    
    /**
     * Rating in terms of number of stars from 1 to 5, that was given in the review.
     */
    protected long stars;

    public Review(String publisherId, String text, Long stars) {
        super(publisherId, text);

        this.setStars(stars);
    }

    public Review(String reviewId, String publisherId, String text, Long stars,
                Long likeNumber, Long dislikeNumber, Long commentNumber, Date datetime)
            throws IllegalArgumentException {

        super(reviewId, publisherId, text, likeNumber, dislikeNumber, commentNumber, datetime);

        this.setStars(stars);

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

}
