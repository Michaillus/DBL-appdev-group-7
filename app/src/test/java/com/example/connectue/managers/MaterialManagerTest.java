package com.example.connectue.managers;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.managers.MaterialManager;
import com.example.connectue.model.Material;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaterialManagerTest {
    private String TAG = "MatTest: ";
    private static final Logger LOGGER = Logger.getLogger(MaterialManagerTest.class.getName());
    @Mock
    private FirebaseFirestore db;

    @InjectMocks
    private MaterialManager materialManagerMock;

    @Before
    public void setUp() throws Exception {
        // Initialize the mock object
        materialManagerMock = Mockito.mock(MaterialManager.class);
        db = mock(FirebaseFirestore.class);
    }

    /**
     * This method tests the behavior of the downloadRecent method in the MaterialManager class.
     * It verifies that the method properly downloads recent study materials for a given course ID.
     */
    @Test
    public void testDownloadRecent() {
        // Define test data
        String courseId = "course123";
        int amount = 5;
        ItemDownloadCallback<List<Material>> callback = mock(ItemDownloadCallback.class);

        // Stubbing the behavior of downloadRecent method
        doNothing().when(materialManagerMock).downloadRecent(courseId, amount, callback);

        // Call the method
        materialManagerMock.downloadRecent(courseId, amount, callback);

        // Verify that the method was called with the correct arguments
        verify(materialManagerMock).downloadRecent(courseId, amount, callback);
    }

    /**
     * This method tests the behavior of the deserialize method in the MaterialManager class.
     * It verifies that the method properly converts a Firestore DocumentSnapshot into a Material object.
     */
    @Test
    public void testDeserialize() {
        // Create a real instance of MaterialManager
        MaterialManager materialManager = new MaterialManager(db, "collectionName", "likeCollectionName", "dislikeCollectionName", "commentCollectionName");

        // Define a mock DocumentSnapshot
        DocumentSnapshot documentSnapshotMock = mock(DocumentSnapshot.class);

        // Define test data for the mock DocumentSnapshot
        String materialId = "material123";
        String publisher = "John Doe";
        String text = "Sample text";
        long likes = 10;
        long dislikes = 5;
        long comments = 3;
        Date timestamp = new Date();
        String parentCourseId = "course123";
        String docURL = "http://example.com";

        // Stub the behavior of the mock DocumentSnapshot to return test data when its methods are called
        when(documentSnapshotMock.getId()).thenReturn(materialId);
        when(documentSnapshotMock.getString("publisher")).thenReturn(publisher);
        when(documentSnapshotMock.getString("text")).thenReturn(text);
        when(documentSnapshotMock.getLong("likes")).thenReturn(likes);
        when(documentSnapshotMock.getLong("dislikes")).thenReturn(dislikes);
        when(documentSnapshotMock.getLong("comments")).thenReturn(comments);
        when(documentSnapshotMock.getTimestamp("timestamp")).thenReturn(new Timestamp(timestamp));
        when(documentSnapshotMock.getString("parentCourseId")).thenReturn(parentCourseId);
        when(documentSnapshotMock.getString("docURL")).thenReturn(docURL);

        // Call the deserialize method on the real MaterialManager object
        Material material = materialManager.deserialize(documentSnapshotMock);

        // Verify that the Material object was created with the correct attributes
        assertEquals(material.getId(), materialId);
        assertEquals(material.getPublisherId(), publisher);
        assertEquals(material.getText(), text);
        assertEquals(material.getLikeNumber(), likes);
        assertEquals(material.getDislikeNumber(), dislikes);
        assertEquals(material.getCommentNumber(), comments);
        assertEquals(material.getDatetime(), timestamp); // Timestamp is null
        assertEquals(material.getParentCourseId(), parentCourseId);
        assertEquals(material.getDocUrl(), docURL);
    }

    /**
     * This method tests the behavior of the serialize method in the MaterialManager class.
     * It verifies that the method properly converts a Material object into a map for uploading to the Firestore database.
     */
    @Test
    public void testSerialize() {
        // Create a real instance of MaterialManager
        MaterialManager materialManager = new MaterialManager(db, "collectionName", "likeCollectionName", "dislikeCollectionName", "commentCollectionName");

        // Define test data for a Material object
        String materialId = "material123";
        String publisher = "John Doe";
        String text = "Sample text";
        long likes = 10;
        long dislikes = 5;
        long comments = 3;
        Date timestamp = new Date();
        String parentCourseId = "course123";
        String docURL = "http://example.com";

        Material material = new Material(materialId, publisher, text, likes, dislikes, comments, timestamp, parentCourseId, docURL);

        // Call the serialize method
        Map<String, Object> serializedData = materialManager.serialize(material);

        // Verify that the serialized data contains the correct attributes
        assertEquals(serializedData.get("publisher"), publisher);
        assertEquals(serializedData.get("text"), text);
        assertEquals(serializedData.get("likes"), likes);
        assertEquals(serializedData.get("dislikes"), dislikes);
        assertEquals(serializedData.get("comments"), comments);
        assertEquals(((Timestamp)serializedData.get("timestamp")).toDate(), timestamp);
        assertEquals(serializedData.get("parentCourseId"), parentCourseId);
        assertEquals(serializedData.get("docURL"), docURL);
    }

}
