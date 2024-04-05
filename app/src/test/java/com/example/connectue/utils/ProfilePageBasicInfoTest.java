package com.example.connectue.utils;

import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProfilePageBasicInfoTest {
    ProfilePageBasicInfo profilePageBasicInfo;
    @Before
    public void setUp() {
        profilePageBasicInfo = new ProfilePageBasicInfo(null,null,null,null,null,null);
    }

//  test the get context function.
    @Test
    public void testGetContext() {
        assertNull(profilePageBasicInfo.getContext());
    }

//    test the get view function.
    @Test
    public void testGetView() {
        assertNull(profilePageBasicInfo.getView());
    }

//    test the get document function.
    @Test
    public void testGetDocument() {
        assertNull(profilePageBasicInfo.getDocument());
    }

//    test the get resource function.
    @Test
    public void testGetResource() {
        assertNull(profilePageBasicInfo.getResources());
    }

//    test the get the database function.
    @Test
    public void testGetDataBase() {
        assertNull(profilePageBasicInfo.getDataBase());
    }

//    test the get user reference function.
    @Test
    public void testGetUser() {
        assertNull(profilePageBasicInfo.getUser());
    }
}
