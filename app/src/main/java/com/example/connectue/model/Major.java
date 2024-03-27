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
     * Name of major ,code in the majors collection.
     */
    public static final String MAJOR_CODE_ATTRIBUTE = "majorCode";

    /**
     * Name of the major.
     */
    public String majorName;

    /**
     * Code (abbreviation) of the major.
     */
    public String majorCode;

    public Major(String majorName, String majorCode) {
        this.majorName = majorName;
        this.majorCode = majorCode;
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
}
