package com.example.connectue.utils;

import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProfilePageSignoutDeleteTest {
    ProfilePageSignoutDelete profilePageSignoutDelete;

    @Before
    public void setUp() {
        profilePageSignoutDelete = new ProfilePageSignoutDelete(null,null,null,null,null);
    }

    //  test the get context function.
    @Test
    public void testGetContext() {
        assertNull(profilePageSignoutDelete.getContext());
    }

    //    test the get view function.
    @Test
    public void testGetView() {
        assertNull(profilePageSignoutDelete.getView());
    }

    @Test
    public void testActivity() {
        assertNull(profilePageSignoutDelete.getActivity());
    }

    @Test
    public void testGetDataBase() {
        assertNull(profilePageSignoutDelete.getDataBase());
    }

    //    test the get user reference function.
    @Test
    public void testGetUser() {
        assertNull(profilePageSignoutDelete.getUser());
    }
}
