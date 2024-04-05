package com.example.connectue.managers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import com.example.connectue.model.Post;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class PostManagerTest {

    // Mock objects that PostManager depends on
    @Mock
    DocumentSnapshot mockDocument;
    @Mock
    PostManager postManager;
    @Mock
    Timestamp timestamp;

    /**
     * Setup mock objects before the tests.
     */
    @Before
    public void setUp() {
        FirebaseFirestore db = Mockito.mock(FirebaseFirestore.class);
        postManager = new PostManager(db, "post",
                "like", "dislike", "comment");

    }

    /**
     * Test the deserialize method which convert database document into a post.
     */
    @Test
    public void deserialize() {
        Long num = 0L;
        Date date = new Date();

        // Mock document get methods so that it doesn't return null
        when(mockDocument.getId()).thenReturn("");
        when(mockDocument.getString("publisher")).thenReturn("");
        when(mockDocument.getString("text")).thenReturn("");
        when(mockDocument.getString("photoURL")).thenReturn("");
        when(mockDocument.getLong("likes")).thenReturn(num);
        when(mockDocument.getLong("comments")).thenReturn(num);
        when(mockDocument.getTimestamp("timestamp")).thenReturn(timestamp);
        when(timestamp.toDate()).thenReturn(date);
        // Assert the new post return is not null.
        assertNotNull(postManager.deserialize(mockDocument));
    }

    /**
     * Test the serialize method which convert a post to a map for uploading
     * to the post collection.
     */
    @Test
    public void serialize() {
        Post post = new Post("postId", "test", "text", "",
                0L, 0L, 0L, new Date());
        assertNotNull(postManager.serialize(post));
    }
}