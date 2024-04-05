package com.example.connectue.utils;

import static org.junit.Assert.*;

import android.widget.Button;

import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ProfilePageHistoryFunctionTest {

    ProfilePageHistoryFunction profilePageHistoryFunction;

    @Before
    public void setUp() {
        profilePageHistoryFunction = new ProfilePageHistoryFunction(null, null,
                null, Mockito.mock(DocumentSnapshot.class));
    }

    @Test
    public void getActivity() {
        assertNull(profilePageHistoryFunction.getActivity());
    }

    @Test
    public void getView() {
        assertNull(profilePageHistoryFunction.getView());
    }

    @Test
    public void getDocument() {
        assertNotNull(profilePageHistoryFunction.getDocument());
    }

    @Test
    public void getRole() {
        assertTrue(profilePageHistoryFunction.getRole() == 0 ||
                profilePageHistoryFunction.getRole() == 2);
    }

    @Test
    public void setRole() {
        profilePageHistoryFunction.setRole(0);
        assertEquals(profilePageHistoryFunction.getRole(), 0);
    }

    @Test
    public void getPostHisBtn() {
        assertNull(profilePageHistoryFunction.getPostHisBtn());
    }

    @Test
    public void setPostHisBtn() {
        Button mockButton = Mockito.mock(Button.class);
        profilePageHistoryFunction.setPostHisBtn(mockButton);
        assertNotNull(profilePageHistoryFunction.getPostHisBtn());
    }

    @Test
    public void getReviewHisBtn() {
    }

    @Test
    public void setReviewHisBtn() {
    }
}