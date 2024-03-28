package com.example.connectue.model;

public class Course {

    /**
     * Name of courses collection in the database.
     */
    public static final String COURSE_COLLECTION_NAME = "courses";

    /**
     * Name of review collection in the database.
     */
    private static final String COURSE_REVIEW_COLLECTION_NAME = "course-reviews";

    /**
     * Name of review likes collection in the database.
     */
    private static final String COURSE_REVIEW_LIKE_COLLECTION_NAME = "course-reviews-likes";

    /**
     * Name of review dislikes collection in the database.
     */
    private static final String COURSE_REVIEW_DISLIKE_COLLECTION_NAME = "course-reviews-dislikes";

    /**
     * Name of review comments collection in the database.
     */
    private static final String COURSE_REVIEW_COMMENT_COLLECTION_NAME = "course-reviews-comments";



    /**
     * Name of majors collection in the database.
     */
    public static final String MAJOR_COLLECTION_NAME = "majors";

    /**
     * Name of review collection in the database.
     */
    private static final String MAJOR_REVIEW_COLLECTION_NAME = "major-reviews";

    /**
     * Name of review likes collection in the database.
     */
    private static final String MAJOR_REVIEW_LIKE_COLLECTION_NAME = "major-reviews-likes";

    /**
     * Name of review dislikes collection in the database.
     */
    private static final String MAJOR_REVIEW_DISLIKE_COLLECTION_NAME = "major-reviews-dislikes";

    /**
     * Name of review comments collection in the database.
     */
    private static final String MAJOR_REVIEW_COMMENT_COLLECTION_NAME = "major-reviews-comments";



    /**
     * Name of course name attribute in the courses collection.
     */
    public static final String NAME_ATTRIBUTE = "name";

    /**
     * Name of course code attribute in the courses collection.
     */
    public static final String CODE_ATTRIBUTE = "code";

    /**
     * Name of rating sum attribute in the courses collection.
     */
    public static final String RATING_SUM_ATTRIBUTE = "ratingSum";

    /**
     * Name of rating number attribute in the courses collection.
     */
    public static final String RATING_NUMBER_ATTRIBUTE = "ratingNumber";

    /**
     * Id of the course.
     */
    private String id;

    /**
     * Full name of the course.
     */
    private String name;

    /**
     * Code (abbreviation) of the course.
     */
    private String code;

    /**
     * Sum of stars over all ratings of the course.
     */
    private long ratingSum;

    /**
     * Number of ratings on the course.
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

    public Course(String name, String code, StudyUnitType type) {
        this("0", name, code, 0L, 0L, type);
    }

    public Course(String id, String name, String code, Long ratingSum,
                  Long ratingNumber, StudyUnitType type) {
        this.name = name;
        this.code = code;
        this.id = id;
        this.ratingSum = ratingSum;
        this.ratingNumber = ratingNumber;
        this.type = type;
    }

    /**
     * Returns the id of the course.
     * @return courseId.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of the course.
     * @return courseName.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the code of the course.
     * @return courseCode;
     */
    public String getCode() {
        return code;
    }

    /**
     * Getter for sum of ratings of the course.
     * @return Sum of ratings.
     */
    public long getRatingSum() {
        return ratingSum;
    }

    /**
     * Setter for sum of ratings of the course.
     * @ratingSum Sum of ratings.
     */
    public void setRatingSum(long ratingSum) {
        this.ratingSum = ratingSum;
    }

    /**
     * Getter for number of ratings of the course.
     * @return Number of ratings.
     */
    public long getRatingNumber() {
        return ratingNumber;
    }

    /**
     * Setter for number of ratings of the course.
     * @param ratingNumber Number of ratings.
     */
    public void setRatingNumber(long ratingNumber) {
        this.ratingNumber = ratingNumber;
    }

    /**
     * Calculates average rating of the course.
     * @return Average rating of the course.
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
     * Converts course model to a string.
     * @return String representing the course.
     */
    public String courseToString() {
        String s = id + "#" + name + "#" + code + "#" + ratingSum + "#" + ratingNumber + "#";
        if (type == StudyUnitType.MAJOR) {
            s += "major";
        } else {
            s += "course";
        }
        return s;
    }

    /**
     * Converts string representing a course to the course model.
     * @param string String representing a course.
     * @return Course model.
     */
    public static Course stringToCourse(String string) {
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
        return new Course(id, name, code, ratingSum, ratingNumber, type);
    }

    public String getStudyUnitCollectionName() {
        if (type == StudyUnitType.COURSE) {
            return COURSE_COLLECTION_NAME;
        } else {
            return MAJOR_COLLECTION_NAME;
        }
    }

    public String getReviewCollectionName() {
        if (type == StudyUnitType.COURSE) {
            return COURSE_REVIEW_COLLECTION_NAME;
        } else {
            return MAJOR_REVIEW_COLLECTION_NAME;
        }
    }

    public String getReviewLikeCollectionName() {
        if (type == StudyUnitType.COURSE) {
            return COURSE_REVIEW_LIKE_COLLECTION_NAME;
        } else {
            return MAJOR_REVIEW_LIKE_COLLECTION_NAME;
        }
    }

    public String getReviewDislikeCollectionName() {
        if (type == StudyUnitType.COURSE) {
            return COURSE_REVIEW_DISLIKE_COLLECTION_NAME;
        } else {
            return MAJOR_REVIEW_DISLIKE_COLLECTION_NAME;
        }
    }

    public String getReviewCommentCollectionName() {
        if (type == StudyUnitType.COURSE) {
            return COURSE_REVIEW_COMMENT_COLLECTION_NAME;
        } else {
            return MAJOR_REVIEW_COMMENT_COLLECTION_NAME;
        }
    }
}
