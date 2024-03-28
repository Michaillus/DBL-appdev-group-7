package com.example.connectue.model;

public class Major {

    /**
     * Name of majors collection in the database.
     */
    public static final String MAJOR_COLLECTION_NAME = "majors";

    /**
     * Name of major name in the majors collection.
     */
    public static final String MAJOR_NAME_ATTRIBUTE = "majorName";

    /**
     * Name of major code in the majors collection.
     */
    public static final String MAJOR_CODE_ATTRIBUTE = "majorCode";

    /**
     * Name of major rating sum attribute in the majors collection.
     */
    public static final String RATING_SUM_ATTRIBUTE = "ratingSum";

    /**
     * Name of major rating number attribute in the majors collection.
     */
    public static final String RATING_NUMBER_ATTRIBUTE = "ratingNumber";

    /**
     * Id of the major.
     */
    private String majorId;

    /**
     * Full name of the major.
     */
    private String majorName;

    /**
     * Code (abbreviation) of the major.
     */
    private String majorCode;

    /**
     * Sum of stars over all ratings of the major.
     */
    private long ratingSum;

    /**
     * Number of ratings on the major.
     */
    private long ratingNumber;

    public Major(String majorName, String majorCode) {
        this.majorName = majorName;
        this.majorCode = majorCode;
    }

    public Major(String majorId, String majorName, String majorCode, Long ratingSum, Long ratingNumber) {
        this.majorId = majorId;
        this.majorName = majorName;
        this.majorCode = majorCode;
        this.ratingSum = ratingSum;
        this.ratingNumber = ratingNumber;
    }

    /**
     * Getter for major id.
     * @return Major id.
     */
    public String getMajorId() {
        return majorId;
    }

    /**
     * Getter for major name.
     * @return Major name.
     */
    public String getMajorName() {
        return majorName;
    }

    /**
     * Getter for major code.
     * @return Major code.
     */
    public String getMajorCode() {
        return majorCode;
    }

    public long getRatingSum() {
        return ratingSum;
    }

    public long getRatingNumber() {
        return ratingNumber;
    }
}
