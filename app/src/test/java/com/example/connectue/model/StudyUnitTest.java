package com.example.connectue.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class StudyUnitTest {

    StudyUnit studyUnit;

    String id = "2957437385";

    String name = "Analysis";

    String code = "2WA30";

    long ratingSum = 40;

    long ratingNumber = 10;

    StudyUnit.StudyUnitType type = StudyUnit.StudyUnitType.COURSE;

    public StudyUnitTest() {
        studyUnit = new StudyUnit(id, name, code, ratingSum, ratingNumber, type);
    }
    /**
     * Test id getter.
     */
    @Test
    public void getId() {
        String result = studyUnit.getId();
        String expected = id;
        assertEquals(result, expected);
    }

    @Test
    public void getName() {
        String result = studyUnit.getName();
        String expected = name;
        assertEquals(result, expected);
    }

    @Test
    public void getCode() {
        String result = studyUnit.getCode();
        String expected = code;
        assertEquals(result, expected);
    }

    @Test
    public void getRatingSum() {
        long result = studyUnit.getRatingSum();
        long expected = ratingSum;
        assertEquals(result, expected);
    }

    @Test
    public void setRatingSum() {
        long expected = 57320948;
        studyUnit.setRatingSum(expected);
        long result = studyUnit.getRatingSum();
        assertEquals(result, expected);
    }

    @Test
    public void getRatingNumber() {
        long result = studyUnit.getRatingNumber();
        long expected = ratingNumber;
        assertEquals(result, expected);
    }

    @Test
    public void setRatingNumber() {
        long expected = 578;
        studyUnit.setRatingNumber(expected);
        long result = studyUnit.getRatingNumber();
        assertEquals(result, expected);
    }

    @Test
    public void getAverageRating() {
        float expected = (float) 40 / (float) 10;
        float result = studyUnit.getAverageRating();
        assertEquals((int) result, (int) expected);
    }

    @Test
    public void getType() {
        StudyUnit.StudyUnitType result = studyUnit.getType();
        StudyUnit.StudyUnitType expected = type;
        assertEquals(result, expected);
    }

    @Test
    public void studyUnitToStringFromString() {
        StudyUnit studyUnit2 = StudyUnit.stringToStudyUnit(studyUnit.studyUnitToString());
        assertEquals(studyUnit.getId(), studyUnit2.getId());
        assertEquals(studyUnit.getName(), studyUnit2.getName());
        assertEquals(studyUnit.getCode(), studyUnit2.getCode());
    }

    @Test
    public void getStudyUnitCollectionName() {
        String result = studyUnit.getStudyUnitCollectionName();
        String expected = StudyUnit.COURSE_COLLECTION_NAME;
        assertEquals(result, expected);
    }

    @Test
    public void getReviewCollectionName() {
        String result = studyUnit.getReviewCollectionName();
        String expected = StudyUnit.COURSE_REVIEW_COLLECTION_NAME;
        assertEquals(result, expected);
    }

    @Test
    public void getReviewLikeCollectionName() {
        String result = studyUnit.getReviewLikeCollectionName();
        String expected = StudyUnit.COURSE_REVIEW_LIKE_COLLECTION_NAME;
        assertEquals(result, expected);
    }

    @Test
    public void getReviewDislikeCollectionName() {
        String result = studyUnit.getReviewDislikeCollectionName();
        String expected = StudyUnit.COURSE_REVIEW_DISLIKE_COLLECTION_NAME;
        assertEquals(result, expected);
    }

    @Test
    public void getReviewCommentCollectionName() {
        String result = studyUnit.getReviewCommentCollectionName();
        String expected = StudyUnit.COURSE_REVIEW_COMMENT_COLLECTION_NAME;
        assertEquals(result, expected);
    }

    @Test
    public void getCollectionName() {
        assertEquals(StudyUnit.getCollectionName(StudyUnit.StudyUnitType.COURSE),
                StudyUnit.COURSE_COLLECTION_NAME);
        assertEquals(StudyUnit.getCollectionName(StudyUnit.StudyUnitType.MAJOR),
                StudyUnit.MAJOR_COLLECTION_NAME);
    }

    @Test
    public void testGetReviewCollectionName() {
        assertEquals(StudyUnit.getReviewCollectionName(StudyUnit.StudyUnitType.COURSE),
                StudyUnit.COURSE_REVIEW_COLLECTION_NAME);
        assertEquals(StudyUnit.getReviewCollectionName(StudyUnit.StudyUnitType.MAJOR),
                StudyUnit.MAJOR_REVIEW_COLLECTION_NAME);
    }

    @Test
    public void testGetReviewLikeCollectionName() {
        assertEquals(StudyUnit.getReviewLikeCollectionName(StudyUnit.StudyUnitType.COURSE),
                StudyUnit.COURSE_REVIEW_LIKE_COLLECTION_NAME);
        assertEquals(StudyUnit.getReviewLikeCollectionName(StudyUnit.StudyUnitType.MAJOR),
                StudyUnit.MAJOR_REVIEW_LIKE_COLLECTION_NAME);
    }

    @Test
    public void testGetReviewDislikeCollectionName() {
        assertEquals(StudyUnit.getReviewDislikeCollectionName(StudyUnit.StudyUnitType.COURSE),
                StudyUnit.COURSE_REVIEW_DISLIKE_COLLECTION_NAME);
        assertEquals(StudyUnit.getReviewDislikeCollectionName(StudyUnit.StudyUnitType.MAJOR),
                StudyUnit.MAJOR_REVIEW_DISLIKE_COLLECTION_NAME);
    }

    @Test
    public void testGetReviewCommentCollectionName() {
        assertEquals(StudyUnit.getReviewCommentCollectionName(StudyUnit.StudyUnitType.COURSE),
                StudyUnit.COURSE_REVIEW_COMMENT_COLLECTION_NAME);
        assertEquals(StudyUnit.getReviewCommentCollectionName(StudyUnit.StudyUnitType.MAJOR),
                StudyUnit.MAJOR_REVIEW_COMMENT_COLLECTION_NAME);
    }
}