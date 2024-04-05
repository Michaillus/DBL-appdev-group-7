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
    }

    @Test
    public void getReviewCollectionName() {
    }

    @Test
    public void getReviewLikeCollectionName() {
    }

    @Test
    public void getReviewDislikeCollectionName() {
    }

    @Test
    public void getReviewCommentCollectionName() {
    }

    @Test
    public void getCollectionName() {
    }

    @Test
    public void testGetReviewCollectionName() {
    }

    @Test
    public void testGetReviewLikeCollectionName() {
    }

    @Test
    public void testGetReviewDislikeCollectionName() {
    }

    @Test
    public void testGetReviewCommentCollectionName() {
    }
}