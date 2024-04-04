package com.example.connectue.model;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class Question extends Interactable{

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
    public static final String QUESTION_COMMENT_COLLECTION_NAME = "questions-replies";

    /**
     * Id of the course for which question is asked.
     */
    protected String parentCourseId;


    /**
     * Constructor for a question that is not yet in the database.
     * @param publisherId Id of the publisher.
     * @param text Text of the question.
     * @param parentCourseId Id of parent interactable.
     */
    public Question(String publisherId, String text, String parentCourseId) {
        super(publisherId, text);

        setParentCourseId(parentCourseId);

        // Setting class tag for logs.
        tag = "Question Model";
    }

    /**
     * Constructor for a question that is already in the database.
     * @param questionId Id of the question.
     * @param publisherId Id of the publisher.
     * @param text Text of the question.
     * @param likeNumber Number of likes got of the question.
     * @param dislikeNumber Number of dislikes got of the question.
     * @param commentNumber Number of comments(unused) of the question.
     * @param datetime Date and time of creation.
     * @param parentCourseId Id of parent interactable.
     * @throws IllegalArgumentException Thrown if any of the parameters are null.
     */
    public Question(String questionId, String publisherId, String text, Long likeNumber,
                    Long dislikeNumber, Long commentNumber, Date datetime, String parentCourseId)
            throws IllegalArgumentException {

        super(questionId, publisherId, text, likeNumber, dislikeNumber, commentNumber, datetime);

        setParentCourseId(parentCourseId);

        // Setting class tag for logs.
        tag = "Question Model";
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