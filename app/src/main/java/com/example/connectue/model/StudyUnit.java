package com.example.connectue.model;

/**
 * Model for a study unit, there are two types of study units - courses and majors.
 */
public class StudyUnit {

    /**
     * Name of courses collection in the database.
     */
    public static final String COURSE_COLLECTION_NAME = "courses";

    /**
     * Name of study unit review collection in the database.
     */
    public static final String COURSE_REVIEW_COLLECTION_NAME = "course-reviews";

    /**
     * Name of course review likes collection in the database.
     */
    public static final String COURSE_REVIEW_LIKE_COLLECTION_NAME = "course-reviews-likes";

    /**
     * Name of course review dislikes collection in the database.
     */
    public static final String COURSE_REVIEW_DISLIKE_COLLECTION_NAME = "course-reviews-dislikes";

    /**
     * Name of course review comments collection in the database.
     */
    public static final String COURSE_REVIEW_COMMENT_COLLECTION_NAME = "course-reviews-comments";



    /**
     * Name of majors collection in the database.
     */
    public static final String MAJOR_COLLECTION_NAME = "majors";

    /**
     * Name of major review collection in the database.
     */
    public static final String MAJOR_REVIEW_COLLECTION_NAME = "major-reviews";

    /**
     * Name of major review likes collection in the database.
     */
    public static final String MAJOR_REVIEW_LIKE_COLLECTION_NAME = "major-reviews-likes";

    /**
     * Name of major review dislikes collection in the database.
     */
    public static final String MAJOR_REVIEW_DISLIKE_COLLECTION_NAME = "major-reviews-dislikes";

    /**
     * Name of major review comments collection in the database.
     */
    public static final String MAJOR_REVIEW_COMMENT_COLLECTION_NAME = "major-reviews-comments";



    /**
     * Name of study unit name attribute in the courses collection.
     */
    public static final String NAME_ATTRIBUTE = "name";

    /**
     * Name of study code attribute in the courses collection.
     */
    public static final String CODE_ATTRIBUTE = "code";

    /**
     * Name of study unit rating sum attribute in the courses collection.
     */
    public static final String RATING_SUM_ATTRIBUTE = "ratingSum";

    /**
     * Name of study unit rating number attribute in the courses collection.
     */
    public static final String RATING_NUMBER_ATTRIBUTE = "ratingNumber";

    /**
     * Id of the study unit.
     */
    private String id;

    /**
     * Full name of the study unit.
     */
    private String name;

    /**
     * Code (abbreviation) of the study unit.
     */
    private String code;

    /**
     * Sum of stars over all ratings of the study unit.
     */
    private long ratingSum;

    /**
     * Number of ratings on the study unit.
     */
    private long ratingNumber;

    /**
     * Enumerator for the type of study unit.
     */
    public enum StudyUnitType {
        COURSE, MAJOR
    }

    /**
     * Type of the study unit - course or major.
     */
    private StudyUnitType type;

    /**
     * Constructor for a study unit that does not (yet) exist in the database.
     * @param name Name of the study unit.
     * @param code Code (abbreviation) of the study unit.
     * @param type Type of the study unit.
     */
    public StudyUnit(String name, String code, StudyUnitType type) {
        this("0", name, code, 0L, 0L, type);
    }

    /**
     * Constructor for a study unit existing in the database.
     * @param id Database id of the study unit.
     * @param name Name of the study unit.
     * @param code Code (abbreviation) of the study unit.
     * @param ratingSum Sum of the star ratings for the study unit.
     * @param ratingNumber Number of ratings given to the study unit.
     * @param type Type of the study unit - course or major.
     */
    public StudyUnit(String id, String name, String code, Long ratingSum,
                     Long ratingNumber, StudyUnitType type) {
        this.name = name;
        this.code = code;
        this.id = id;
        this.ratingSum = ratingSum;
        this.ratingNumber = ratingNumber;
        this.type = type;
    }

    /**
     * Returns the id of the study unit.
     * @return courseId.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of the study unit.
     * @return courseName.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the code of the study unit.
     * @return courseCode;
     */
    public String getCode() {
        return code;
    }

    /**
     * Getter for sum of ratings of the study unit.
     * @return Sum of ratings.
     */
    public long getRatingSum() {
        return ratingSum;
    }

    /**
     * Setter for sum of ratings of the study unit.
     * @ratingSum Sum of ratings.
     */
    public void setRatingSum(long ratingSum) {
        this.ratingSum = ratingSum;
    }

    /**
     * Getter for number of ratings of the study unit.
     * @return Number of ratings.
     */
    public long getRatingNumber() {
        return ratingNumber;
    }

    /**
     * Setter for number of ratings of the study unit.
     * @param ratingNumber Number of ratings.
     */
    public void setRatingNumber(long ratingNumber) {
        this.ratingNumber = ratingNumber;
    }

    /**
     * Calculates average rating of the study unit.
     * @return Average rating.
     */
    public Float getAverageRating() {
        if (ratingNumber == 0) {
            return (float) 0;
        } else {
            return (float) ratingSum / (float) ratingNumber;
        }
    }

    /**
     * Getter for the type of study unit.
     * @return Type of study unit.
     */
    public StudyUnitType getType() {
        return type;
    }

    /**
     * Converts study unit model to a string.
     * @return String representing the study unit.
     */
    public String studyUnitToString() {
        String s = id + "#" + name + "#" + code + "#" + ratingSum + "#" + ratingNumber + "#";
        if (type == StudyUnitType.MAJOR) {
            s += "major";
        } else {
            s += "course";
        }
        return s;
    }

    /**
     * Converts string representing a study unit to the study unit model.
     * @param string String representing a study unit.
     * @return Study unit model.
     */
    public static StudyUnit stringToStudyUnit(String string) {
        String[] parts = string.split("#");
        String id = parts[0];
        String name = parts[1];
        String code = parts[2];
        long ratingSum = Long.parseLong(parts[3]);
        long ratingNumber = Long.parseLong(parts[4]);
        StudyUnitType type;
        if (parts[5].equals("course")) {
            type = StudyUnitType.COURSE;
        } else {
            type = StudyUnitType.MAJOR;
        }
        return new StudyUnit(id, name, code, ratingSum, ratingNumber, type);
    }

    /**
     * Getter for the name of collection in the database that stores study units of {@code type}.
     * @return Name of collection of the study unit.
     */
    public String getStudyUnitCollectionName() {
        if (type == StudyUnitType.COURSE) {
            return COURSE_COLLECTION_NAME;
        } else {
            return MAJOR_COLLECTION_NAME;
        }
    }

    /**
     * Getter for the name of review collection in the database that stores reviews for
     * study units of {@code type}.
     * @return Name of collection of reviews of the study unit.
     */
    public String getReviewCollectionName() {
        if (type == StudyUnitType.COURSE) {
            return COURSE_REVIEW_COLLECTION_NAME;
        } else {
            return MAJOR_REVIEW_COLLECTION_NAME;
        }
    }

    /**
     * Getter for the name of like collection in the database that stores likes for
     * study units of {@code type}.
     * @return Name of collection of likes of the study unit.
     */
    public String getReviewLikeCollectionName() {
        if (type == StudyUnitType.COURSE) {
            return COURSE_REVIEW_LIKE_COLLECTION_NAME;
        } else {
            return MAJOR_REVIEW_LIKE_COLLECTION_NAME;
        }
    }

    /**
     * Getter for the name of dislike collection in the database that stores dislikes for
     * study units of {@code type}.
     * @return Name of collection of dislikes of the study unit.
     */
    public String getReviewDislikeCollectionName() {
        if (type == StudyUnitType.COURSE) {
            return COURSE_REVIEW_DISLIKE_COLLECTION_NAME;
        } else {
            return MAJOR_REVIEW_DISLIKE_COLLECTION_NAME;
        }
    }

    /**
     * Getter for the name of comment collection in the database that stores comments for
     * study units of {@code type}.
     * @return Name of collection of comments of the study unit.
     */
    public String getReviewCommentCollectionName() {
        if (type == StudyUnitType.COURSE) {
            return COURSE_REVIEW_COMMENT_COLLECTION_NAME;
        } else {
            return MAJOR_REVIEW_COMMENT_COLLECTION_NAME;
        }
    }

    /**
     * Returns study unit collection name depending on a type of study unit.
     * @param type Type of a study unit.
     * @return Collection name.
     */
    public static String getCollectionName(StudyUnitType type) {
        if (type == StudyUnitType.COURSE) {
            return COURSE_COLLECTION_NAME;
        } else {
            return MAJOR_COLLECTION_NAME;
        }
    }

    /**
     * Returns study unit review collection name depending on a type of study unit.
     * @param type Type of a study unit.
     * @return Collection name.
     */
    public static String getReviewCollectionName(StudyUnitType type) {
        if (type == StudyUnitType.COURSE) {
            return COURSE_REVIEW_COLLECTION_NAME;
        } else {
            return MAJOR_REVIEW_COLLECTION_NAME;
        }
    }

    /**
     * Returns study unit review like collection name depending on a type of study unit.
     * @param type Type of a study unit.
     * @return Collection name.
     */
    public static String getReviewLikeCollectionName(StudyUnitType type) {
        if (type == StudyUnitType.COURSE) {
            return COURSE_REVIEW_LIKE_COLLECTION_NAME;
        } else {
            return MAJOR_REVIEW_LIKE_COLLECTION_NAME;
        }
    }

    /**
     * Returns study unit review dislike collection name depending on a type of study unit.
     * @param type Type of a study unit.
     * @return Collection name.
     */
    public static String getReviewDislikeCollectionName(StudyUnitType type) {
        if (type == StudyUnitType.COURSE) {
            return COURSE_REVIEW_DISLIKE_COLLECTION_NAME;
        } else {
            return MAJOR_REVIEW_DISLIKE_COLLECTION_NAME;
        }
    }

    /**
     * Returns study unit review comment collection name depending on a type of study unit.
     * @param type Type of a study unit.
     * @return Collection name.
     */
    public static String getReviewCommentCollectionName(StudyUnitType type) {
        if (type == StudyUnitType.COURSE) {
            return COURSE_REVIEW_COMMENT_COLLECTION_NAME;
        } else {
            return MAJOR_REVIEW_COMMENT_COLLECTION_NAME;
        }
    }
}
