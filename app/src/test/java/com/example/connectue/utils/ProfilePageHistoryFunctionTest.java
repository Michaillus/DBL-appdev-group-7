package com.example.connectue.utils;

import static org.junit.Assert.*;

import android.widget.Button;

import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Class for tests for ProfilePageHistoryFunction
 */
public class ProfilePageHistoryFunctionTest {

    ProfilePageHistoryFunction profilePageHistoryFunction;

    /**
     * Setup mock ProfilePageHistoryFunction used for tests
     */
    @Before
    public void setUp() {
        profilePageHistoryFunction = new ProfilePageHistoryFunction(null, null,
                null, Mockito.mock(DocumentSnapshot.class));
    }

    /**
     * Test for getActivity
     */
    @Test
    public void getActivity() {
        assertNull(profilePageHistoryFunction.getActivity());
    }

    /**
     * Test for getView
     */
    @Test
    public void getView() {
        assertNull(profilePageHistoryFunction.getView());
    }

    /**
     * Test for getDocument
     */
    @Test
    public void getDocument() {
        assertNotNull(profilePageHistoryFunction.getDocument());
    }

    /**
     * Test for getRole
     */
    @Test
    public void getRole() {
        assertTrue(profilePageHistoryFunction.getRole() == 0 ||
                profilePageHistoryFunction.getRole() == 2);
    }

    /**
     * Test for setRole
     */
    @Test
    public void setRole() {
        profilePageHistoryFunction.setRole(0);
        assertEquals(profilePageHistoryFunction.getRole(), 0);
    }

    /**
     * Test for getPostHisBtn
     */
    @Test
    public void getPostHisBtn() {
        assertNull(profilePageHistoryFunction.getPostHisBtn());
    }

    /**
     * Test for setPostHisBtn
     */
    @Test
    public void setPostHisBtn() {
        Button mockButton = Mockito.mock(Button.class);
        profilePageHistoryFunction.setPostHisBtn(mockButton);
        assertNotNull(profilePageHistoryFunction.getPostHisBtn());
    }

    /**
     * Test for getReviewHisBtn
     */
    @Test
    public void getReviewHisBtn() {
        assertNull(profilePageHistoryFunction.getReviewHisBtn());
    }

    /**
     * Test for setReviewHisBtn
     */
    @Test
    public void setReviewHisBtn() {
        Button mockButton = Mockito.mock(Button.class);
        profilePageHistoryFunction.setReviewHisBtn(mockButton);
        assertNotNull(profilePageHistoryFunction.getReviewHisBtn());
    }
}