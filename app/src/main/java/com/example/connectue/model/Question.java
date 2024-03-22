package com.example.connectue.model;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class Question extends Interactable{

    private static final String TAG = "Question class: ";
    protected String publisherName;

    public Question(String publisherId, String text) {
        super(publisherId, text);
    }

    public Question(String uName, String reviewId, String publisherId, String text,
                  Long likeNumber, Long dislikeNumber, Long commentNumber, Date datetime)
            throws IllegalArgumentException {

        super(reviewId, publisherId, text, likeNumber, dislikeNumber, commentNumber, datetime);
        this.publisherName = uName;
    }

    //Getters and setters for publisher name
    public String getUserName() {return publisherName;}
    public void setUserName(String publisherName) {this.publisherName = publisherName;}
    // Getters and setters for interactable text.

}