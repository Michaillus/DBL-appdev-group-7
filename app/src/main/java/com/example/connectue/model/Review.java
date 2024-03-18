package com.example.connectue.model;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class Review extends Interactable{

    private static final String TAG = "Review class: ";

    protected long stars;

    public Review(String reviewId, String publisherId, String text, Long stars,
                Long likeNumber, Long dislikeNumber, Long commentNumber, Date datetime)
            throws IllegalArgumentException {

        super(reviewId, publisherId, text, likeNumber, dislikeNumber, commentNumber, datetime);

        this.setStars(stars);

    }

//    // Constructor
//    public Review(String uName, String date, String uText, int pLikes, int pDislikes, int stars, String likeNum, String dislikeNum) {
//        this.uName = uName;
//        this.date = date;
//        this.uText = uText;
//        this.pLikes = pLikes;
//        this.pDislikes = pDislikes;
//        this.stars = stars;
//        this.likeNum = likeNum;
//        this.dislikeNum = dislikeNum;
//    }

    // Getters and setters for stars.
    public long getStars() {
        return stars;
    }
    public void setStars(long stars) {
        this.stars = stars;
    }

}
