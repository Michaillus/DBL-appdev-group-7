package com.example.connectue.model;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class Question extends Interactable{

    private static final String TAG = "Question class: ";

    public Question(String publisherId, String text) {
        super(publisherId, text);
    }

    public Question(String reviewId, String publisherId, String text,
                  Long likeNumber, Long dislikeNumber, Long commentNumber, Date datetime)
            throws IllegalArgumentException {

        super(reviewId, publisherId, text, likeNumber, dislikeNumber, commentNumber, datetime);

    }

}