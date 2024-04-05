package com.example.connectue.managers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import com.example.connectue.model.StudyUnit;
import com.example.connectue.model.User;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Date;

/**
 * Tests for StudyUnitManager class
 */
public class UserManagerTest {

    // Mock objects that the UserManager depends on
    @Mock
    UserManager userManager;
    @Mock
    DocumentSnapshot mockDocument;

    /**
     * Setup mock objects before the tests.
     */
    @Before
    public void setUp() {
        FirebaseFirestore db = Mockito.mock(FirebaseFirestore.class);
        userManager = new UserManager(db, "users");
    }

    /**
     * Test for deserialize method which convert database document into a user.
     */
    @Test
    public void deserialize() {
        Long num = 0L;

        // Mock document
        mockDocument = Mockito.mock(DocumentSnapshot.class);

        // Mock document get methods so that it doesn't return null
        when(mockDocument.getId()).thenReturn("");
        when(mockDocument.getString("firstName")).thenReturn("");
        when(mockDocument.getString("lastName")).thenReturn("");
        when(mockDocument.getBoolean("isVerified")).thenReturn(true);
        when(mockDocument.getString("email")).thenReturn("");
        when(mockDocument.getString("phone")).thenReturn("");
        when(mockDocument.getString("profilePicURL")).thenReturn("");
        when(mockDocument.getString("program")).thenReturn("");
        when(mockDocument.getLong("role")).thenReturn(num);
        // Assert the new post return is not null.
        assertNotNull(userManager.deserialize(mockDocument));
    }

    /**
     * Test the serialize method which convert a user to a map for uploading
     * to the user collection.
     */
    @Test
    public void serialize() {
        User user = new User("", "", "", true,
                "", "", "", "", 1);
        assertNotNull(userManager.serialize(user));
    }
}