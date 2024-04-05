package com.example.connectue.managers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import com.example.connectue.model.StudyUnit;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Date;
import java.util.Map;

/**
 * Tests for StudyUnitManager class
 */
public class StudyUnitManagerTest {

    // Mock objects that the QuestionManager depends on
    @Mock
    StudyUnitManager studyUnitManager;
    @Mock
    DocumentSnapshot mockDocument;

    String id = "92572847";
    String name = "name387394";
    String code = "22222";
    long ratingNumber = 284;
    long ratingSum = 433;

    /**
     * Setup mock objects before the tests.
     */
    @Before
    public void setUp() {
        FirebaseFirestore db = Mockito.mock(FirebaseFirestore.class);
        studyUnitManager = new StudyUnitManager(db, "courses");
    }

    /**
     * Test for deserialize method which convert database document into a study unit.
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
        when(mockDocument.getId()).thenReturn(id);
        when(mockDocument.getString(StudyUnit.NAME_ATTRIBUTE)).thenReturn(name);
        when(mockDocument.getString(StudyUnit.CODE_ATTRIBUTE)).thenReturn(code);
        when(mockDocument.getLong(StudyUnit.RATING_SUM_ATTRIBUTE)).thenReturn(ratingSum);
        when(mockDocument.getLong(StudyUnit.RATING_NUMBER_ATTRIBUTE)).thenReturn(ratingNumber);
        // Assert the new post return is not null.

        StudyUnit studyUnit = studyUnitManager.deserialize(mockDocument);

        assertEquals(id, studyUnit.getId());
        assertEquals(name, studyUnit.getName());
        assertEquals(code, studyUnit.getCode());
        assertEquals(ratingSum, studyUnit.getRatingSum());
        assertEquals(ratingNumber, studyUnit.getRatingNumber());

    }

    /**
     * Test for the getType method that return the type
     */
    @Test
    public void getType() {
        assertEquals(studyUnitManager.getType(), StudyUnit.StudyUnitType.COURSE);
    }

    /**
     * Test for the setType method that set the type.
     */
    @Test
    public void setType() {
        studyUnitManager.setType(StudyUnit.StudyUnitType.COURSE);
        assertEquals(studyUnitManager.getType(), StudyUnit.StudyUnitType.COURSE);
    }

    /**
     * Test the serialize method which convert a study unit to a map for uploading
     * to the study unit collection.
     */
    @Test
    public void serialize() {

        StudyUnit studyUnit = new StudyUnit(id, name, code, ratingSum,
                ratingNumber, StudyUnit.StudyUnitType.COURSE);
        Map<String, Object> map = studyUnitManager.serialize(studyUnit);

        assertEquals(map.get(StudyUnit.CODE_ATTRIBUTE), code);
        assertEquals(map.get(StudyUnit.NAME_ATTRIBUTE), name);
        assertEquals(map.get(StudyUnit.RATING_NUMBER_ATTRIBUTE), ratingNumber);
        assertEquals(map.get(StudyUnit.RATING_SUM_ATTRIBUTE), ratingSum);
    }
}