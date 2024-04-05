package com.example.connectue.managers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import com.example.connectue.model.Question;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Date;

/**
 * Tests for QuestionManager class
 */
public class QuestionManagerTest {

    // Mock objects that the QuestionManager depends on
    @Mock
    QuestionManager questionManager;
    @Mock
    DocumentSnapshot mockDocument;

    /**
     * Setup mock objects before the tests.
     */
    @Before
    public void setUp() {
        FirebaseFirestore db = Mockito.mock(FirebaseFirestore.class);
        questionManager = new QuestionManager(db, "questions",
                "likes", "dislikes",
                "replies");
    }

    /**
     * Test for deserialize method which convert database document into a question.
     */
    @Test
    public void deserialize() {
        Long num = 0L;
        Date date = new Date();

        // Mock timestamp
        Timestamp timestamp = Mockito.mock(Timestamp.class);

        // Mock document
        mockDocument = Mockito.mock(DocumentSnapshot.class);

        // Mock document get methods so that it doesn't return null
        when(mockDocument.getId()).thenReturn("");
        when(mockDocument.getString("publisher")).thenReturn("");
        when(mockDocument.getString("text")).thenReturn("");
        when(mockDocument.getLong("likes")).thenReturn(num);
        when(mockDocument.getLong("dislikes")).thenReturn(num);
        when(mockDocument.getLong("comments")).thenReturn(num);
        when(mockDocument.getTimestamp("timestamp")).thenReturn(timestamp);
        when(timestamp.toDate()).thenReturn(date);
        when(mockDocument.getString("parentCourseId")).thenReturn("");
        // Assert the new post return is not null.
        assertNotNull(questionManager.deserialize(mockDocument));
    }

    /**
     * Test the serialize method which convert a question to a map for uploading
     * to the question collection.
     */
    @Test
    public void serialize() {
        Question question = new Question("", "", "", 0L,
                0L, 0L, new Date(), "");
        assertNotNull(questionManager.serialize(question));
    }
}