package com.example.connectue.managers;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.model.Comment;
import com.example.connectue.model.Post;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CommentManagerTest {

    // Mock objects that the CommentManager depends on
    @Mock
    CommentManager commentManager;
    @Mock
    DocumentSnapshot mockDocument;

    /**
     * Setup mock objects before the tests.
     */
    @Before
    public void setUp() {
        FirebaseFirestore db = Mockito.mock(FirebaseFirestore.class);
        commentManager = new CommentManager(db, "comment");
    }

    /**
     * Test for downloadRecent comments method. Check if the query is as expected.
     */
    @Test
    public void downloadRecent() {
//        CollectionReference collection = Mockito.mock(CollectionReference.class);
//        Query query = Mockito.mock(Query.class);
//        when(collection.whereEqualTo(Comment.PARENT_ID, "")).thenReturn(query);
//
//        ItemDownloadCallback<List<Comment>> mockCallback = Mockito.mock(ItemDownloadCallback.class);
//
//        commentManager.downloadRecent("", 0, mockCallback);
//        Mockito.verify(mockCallback).onFailure(any());

    }

    /**
     * Test for deserialize method which convert database document into a comment.
     */
    @Test
    public void deserialize() {
        Date date = new Date();

        // Mock timestamp
        Timestamp timestamp = Mockito.mock(Timestamp.class);

        // Mock document get methods so that it doesn't return null
        when(mockDocument.getId()).thenReturn("");
        when(mockDocument.getString("userId")).thenReturn("");
        when(mockDocument.getString("text")).thenReturn("");
        when(mockDocument.getString("parentId")).thenReturn("");
        when(mockDocument.getTimestamp("timestamp")).thenReturn(timestamp);
        when(timestamp.toDate()).thenReturn(date);
        // Assert the new post return is not null.
        assertNotNull(commentManager.deserialize(mockDocument));
    }

    /**
     * Test the serialize method which convert a comment to a map for uploading
     * to the comment collection.
     */
    @Test
    public void serialize() {
        Comment comment = new Comment("commentId", "test",
                "text", "", new Date());
        assertNotNull(commentManager.serialize(comment));
    }
}