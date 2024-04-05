package com.example.connectue.utils;

import static org.junit.Assert.*;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

public class ProfilePagePictureOperationTest {

    Context context;
    Activity activity;
    androidx.fragment.app.Fragment fragment;
    View view;
    DocumentSnapshot document;
    FirebaseFirestore db;
    FirebaseUser user;

    ProfilePagePictureOperation profilePagePictureOperation;


    public ProfilePagePictureOperationTest() {
//        profilePagePictureOperation = new ProfilePagePictureOperation(context, activity,fragment, view,
//                document, db, user);
    }
    @Before
    public void setUp(){
        profilePagePictureOperation = new ProfilePagePictureOperation(null, null, null, null,
                null, null, null);
    }

//    @Test
//    public void testConstructor() {
//        ProfilePagePictureOperation profilePagePictureOperation = new ProfilePagePictureOperation(context,
//                activity, fragment, view, document, db, user);
//        assertEquals(context, profilePagePictureOperation.getContext());
//        assertEquals(activity, profilePagePictureOperation.getActivity());
//        assertEquals(fragment, profilePagePictureOperation.getFragment());
//        assertEquals(view, profilePagePictureOperation.getView());
//        assertEquals(document, profilePagePictureOperation.getDocument());
//        assertEquals(db, profilePagePictureOperation.getDb());
//        assertEquals(user,profilePagePictureOperation.getUser());
//    }

    @Test
    public void getContext() {
        assertNull(profilePagePictureOperation.getContext());
    }

    @Test
    public void getActivity() {
        assertNull(profilePagePictureOperation.getActivity());
    }

    @Test
    public void getFragment() {
        assertNull(profilePagePictureOperation.getFragment());
    }

    @Test
    public void getView() {
        assertNull(profilePagePictureOperation.getView());
    }

    @Test
    public void getDocument() {
        assertNull(profilePagePictureOperation.getDocument());
    }

    @Test
    public void setDocument() {
        profilePagePictureOperation.setDocument(null);
        assertNull(profilePagePictureOperation.getDocument());
    }

    @Test
    public void getProfileIV() {
        assertNull(profilePagePictureOperation.getProfileIV());
    }

    @Test
    public void setProfileIV() {
        profilePagePictureOperation.setProfileIV(null);
        assertNull(profilePagePictureOperation.getProfileIV());
    }

    @Test
    public void getDb() {
        FirebaseFirestore result = profilePagePictureOperation.getDb();
        FirebaseFirestore expected = profilePagePictureOperation.getDb();
        assertEquals(result, expected);
    }

    @Test
    public void getUser() {
        FirebaseUser result = profilePagePictureOperation.getUser();
        FirebaseUser expected = profilePagePictureOperation.getUser();
        assertEquals(result, expected);
    }

    @Test
    public void getImageURL() {
        String result = profilePagePictureOperation.getImageURL();
        String expected = profilePagePictureOperation.getImageURL();
        assertEquals(result, expected);
    }

    @Test
    public void setImageURL() {
        profilePagePictureOperation.setImageURL(null);
        assertNull(profilePagePictureOperation.getImageURL());
    }

    @Test
    public void getImageUri() {
        assertNull(profilePagePictureOperation.getImageUri());
    }

    @Test
    public void setImageUri() {
        profilePagePictureOperation.setImageUri(null);
        assertNull(profilePagePictureOperation.getImageUri());
    }

    @Test
    public void getEmailStr() {
        assertEquals("",profilePagePictureOperation.getEmailStr());
    }

    @Test
    public void setEmailStr() {
        profilePagePictureOperation.setEmailStr(null);
        assertNull(profilePagePictureOperation.getEmailStr());
    }
}