package com.example.connectue.managers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Iterator;

public class EntityManagerTest {

    // Mock objects that the UserManager depends on
    @Mock
    EntityManager entityManager;
    @Mock
    QuerySnapshot mockQuerySnapshot;

    /**
     * Setup mock objects before the tests.
     */
    @Before
    public void setUp() {
        FirebaseFirestore db = Mockito.mock(FirebaseFirestore.class);
        entityManager = new UserManager(db, "users");
    }

    /**
     * Test for the method resetLastRetrieved
     */
    @Test
    public void resetLastRetrieved() {
        entityManager.resetLastRetrieved();
        assertNull(entityManager.lastRetrieved);
    }

    /**
     * Test for deserializeList method which convert a list of database documents
     * into a list of entities.
     */
    @Test
    public void deserializeList() {
        mockQuerySnapshot = Mockito.mock(QuerySnapshot.class);
        Iterator iterator = new Iterator() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                return null;
            }
        };
        when(mockQuerySnapshot.iterator()).thenReturn(iterator);
//        when(mockQuerySnapshot.iterator().hasNext()).thenReturn(false);
        assertTrue(entityManager.deserializeList(mockQuerySnapshot).isEmpty());
    }
}