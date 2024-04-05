package com.example.connectue.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class QuestionTest extends InteractableTest {

    Question question;

    String parentCourseId = "957253852793";

    public QuestionTest() {
        question = new Question(id, publisherId, text, likeNumber, dislikeNumber, commentNumber,
                dateTime, parentCourseId);
        interactable = question;
    }

    @Test
    public void testSecondConstructor() {
        // Create a new Question object using the second constructor
        Question question = new Question("publisherId", "text", "parentCourseId");

        // Verify that the fields are set correctly
        assertEquals("publisherId", question.getPublisherId());
        assertEquals("text", question.getText());
        assertEquals("parentCourseId", question.getParentCourseId());

        // Verify that default values are set for other fields
        assertEquals(null, question.getId());
        assertEquals(0, question.getLikeNumber());
        assertEquals(0, question.getDislikeNumber());
        assertEquals(0, question.getCommentNumber());
        assertNotNull(question.getDatetime());
    }


    @Test
    public void getParentCourseId() {
        String result = question.getParentCourseId();
        String expected = parentCourseId;
        assertEquals(result, expected);
    }

    @Test
    public void setParentCourseId() {
        String expected = "3674856484625444";
        question.setParentCourseId(expected);
        String result = question.getParentCourseId();
        assertEquals(result, expected);
    }
}