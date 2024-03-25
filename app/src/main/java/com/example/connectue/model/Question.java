package com.example.connectue.model;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class Question extends Interactable{

    /**
     * Class tag for logs.
     */
    private static final String TAG = "Question class: ";
    protected String publisherName;


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

    /**
     * Id of the course for which question is asked.
     */
    protected String parentCourseId;


    public Question(String publisherId, String text, String parentCourseId) {
        super(publisherId, text);

        setParentCourseId(parentCourseId);
    }

    public Question(String questionId, String publisherId, String text, Long likeNumber,
                    Long dislikeNumber, Long commentNumber, Date datetime, String parentCourseId)
            throws IllegalArgumentException {

        super(questionId, publisherId, text, likeNumber, dislikeNumber, commentNumber, datetime);

        setParentCourseId(parentCourseId);
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