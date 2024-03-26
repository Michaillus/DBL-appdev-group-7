package com.example.connectue.model;

import java.util.Date;

public class Material extends Interactable {

    /**
     * Name of material collection in the database.
     */
    public static final String MATERIAL_COLLECTION_NAME = "materials";

    /**
     * Name of material likes collection in the database.
     */
    public static final String MATERIAL_LIKE_COLLECTION_NAME = "materials-likes";

    /**
     * Name of material dislikes collection in the database.
     */
    public static final String MATERIAL_DISLIKE_COLLECTION_NAME = "materials-dislikes";

    /**
     * Name of material comments collection in the database.
     */
    public static final String MATERIAL_COMMENT_COLLECTION_NAME = "materials-comments";

    protected String parentCourseId;

    protected String docUrl;

    public Material(String publisherId, String text, String parentCourseId, String docUrl) {
        super(publisherId, text);

        setDocUrl(docUrl);
        setParentCourseId(parentCourseId);

        // Setting class tag for logs.
        tag = "Material Model";
    }

    public Material(String materialId, String publisherId, String text,
                  Long likeNumber, Long dislikeNumber, Long commentNumber, Date datetime, String parentCourseId, String docUrl)
            throws IllegalArgumentException {

        super(materialId, publisherId, text, likeNumber, dislikeNumber, commentNumber, datetime);
        setDocUrl(docUrl);
        setParentCourseId(parentCourseId);

        // Setting class tag for logs.
        tag = "Material Model";
    }

    /**
     * Getter for parent course id.
     * @return Parent course id.
     */
    public String getParentCourseId() {
        return parentCourseId;
    }

    /**
     * Setter for parent course id.
     * @param parentCourseId Parent course id.
     */
    public void setParentCourseId(String parentCourseId) {
        this.parentCourseId = parentCourseId;
    }

    /**
     * Getter for document url.
     * @return Document url.
     */
    public String getDocUrl() {return docUrl;}

    /**
     * Setter for document url.
     * @param docUrl Document url.
     */
    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }
}
