package com.example.connectue.model;

import java.util.Date;

public class Material extends Interactable {
    protected String parentCourseId;
    protected String docUrl;

    public Material(String reviewId, String publisherId, String text,
                  Long likeNumber, Long dislikeNumber, Long commentNumber, Date datetime, String parentCourseId, String docUrl)
            throws IllegalArgumentException {

        super(reviewId, publisherId, text, likeNumber, dislikeNumber, commentNumber, datetime);
        this.parentCourseId = parentCourseId;
        this.docUrl = docUrl;
    }

    public String getParentCourseId() {
        return parentCourseId;
    }
    public String getDocUrl() {return docUrl;}


}
