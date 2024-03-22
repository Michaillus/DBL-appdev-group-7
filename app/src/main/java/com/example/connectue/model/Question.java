package com.example.connectue.model;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class Question extends Interactable{

    /**
     * Class tag for logs.
     */
    private static final String TAG = "Question class: ";


    /**
     * Name of question collection in the database.
     */
    public static final String QUESTION_COLLECTION_NAME = "questions";

    /**
     * Name of question likes collection in the database.
     */
    public static final String QUESTION_LIKE_COLLECTION_NAME = "question-likes";

    /**
     * Name of question dislikes collection in the database.
     */
    public static final String QUESTION_DISLIKE_COLLECTION_NAME = "question-dislikes";

    /**
     * Name of question comments collection in the database.
     */
    public static final String QUESTION_COMMENT_COLLECTION_NAME = "question-comments";


    public Question(String publisherId, String text) {
        super(publisherId, text);
    }

    public Question(String questionId, String publisherId, String text,
                  Long likeNumber, Long dislikeNumber, Long commentNumber, Date datetime)
            throws IllegalArgumentException {

        super(questionId, publisherId, text, likeNumber, dislikeNumber, commentNumber, datetime);

    }

}