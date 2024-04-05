package com.example.connectue.utils;

import com.example.connectue.fragments.ManageReportHistoryFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

@RunWith(MockitoJUnitRunner.class)
public class AdminDataManagerTest {
    @Mock
    FirebaseFirestore mockFirestore;
    @Mock
    AdminReportedItemDataManager mockDataManager;

    @Mock
    ManageReportHistoryFragment mockFragment;
    @Mock
    CollectionReference mockCollectionReference;
    @Mock
    Query mockQuery;
    @Mock
    Context mockContext;

    @Mock
    Task<QuerySnapshot> mockTask;

    AdminReportedItemDataManager dataManager;
    FirebaseFirestore db;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        List<QueryDocumentSnapshot> reports = new ArrayList<>();
        dataManager = new AdminReportedItemDataManager(reports, mockFirestore, General.POSTCOLLECTION, mockFragment);
    }

    // To test the current channel get function.
    @Test
    public void testGetCurrentChannel() {
        assertEquals(General.POSTCOLLECTION, dataManager.getCurrentChannel());
    }

    // To test the current channel set function.
    @Test
    public void testSetCurrentChannel() {
        String newChannel = General.POSTCOLLECTION;
        dataManager.setCurrentChannel(newChannel);
        assertEquals(newChannel, dataManager.getCurrentChannel());
    }

    // To test the current documentSnapshot get function.
    @Test
    public void testGetCurrentDoc() {
        assertNull(dataManager.getCurrentDoc());
    }

    // To test the current content reference get function.
    @Test
    public void testGetCurrentContentReference() {
        assertNull(dataManager.getCurrentContentReference());
    }

    // To test the current request reference get function.
    @Test
    public void testGetCurrentRequestReference() {
        assertNull(dataManager.getCurrentRequestReference());
    }
}
