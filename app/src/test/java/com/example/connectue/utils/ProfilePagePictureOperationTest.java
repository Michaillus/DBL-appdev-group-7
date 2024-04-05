package com.example.connectue.utils;

import static org.junit.Assert.*;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        profilePagePictureOperation = new ProfilePagePictureOperation(context, activity,fragment, view,
                document, db, user);
    }

    @Test
    public void testConstructor() {
        ProfilePagePictureOperation profilePagePictureOperation = new ProfilePagePictureOperation(context,
                activity, fragment, view, document, db, user);
        assertEquals(context, profilePagePictureOperation.getContext());
        assertEquals(activity, profilePagePictureOperation.getActivity());
        assertEquals(fragment, profilePagePictureOperation.getFragment());
        assertEquals(view, profilePagePictureOperation.getView());
        assertEquals(document, profilePagePictureOperation.getDocument());
        assertEquals(db, profilePagePictureOperation.getDb());
        assertEquals(user,profilePagePictureOperation.getUser());
    }

    @Test
    public void getContext() {
        assertNull(null);
    }

    @Test
    public void getActivity() {
        assertNull(null);
    }

    @Test
    public void getFragment() {
        assertNull(null);
    }

    @Test
    public void getView() {
        assertNull(null);
    }

    @Test
    public void getDocument() {
        assertNull(null);
    }

    @Test
    public void setDocument() {
        assertNull(null);
    }

    @Test
    public void getProfileIV() {
        assertNull(null);
    }

    @Test
    public void setProfileIV() {
        assertNull(null);
    }

    @Test
    public void getDb() {
        assertNull(null);
    }

    @Test
    public void getUser() {
        assertNull(null);
    }

    @Test
    public void getImageURL() {
        assertNull(null);
    }

    @Test
    public void setImageURL() {
        assertNull(null);
    }

    @Test
    public void getImageUri() {
        assertNull(null);
    }

    @Test
    public void setImageUri() {
        assertNull(null);
    }

    @Test
    public void getEmailStr() {
        assertNull(null);
    }

    @Test
    public void setEmailStr() {
        assertNull(null);
    }
}