package com.example.connectue.managers;

import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.model.Comment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommentManagerTest {
    @Mock
    private FirebaseFirestore db;

    @InjectMocks
    private CommentManager commentManager;

    @Before
    public void setUp() {
        commentManager = new CommentManager(db, "comments");
    }

    @Test
    public void testDeserialize() {
        // Define a mock DocumentSnapshot
        DocumentSnapshot documentSnapshotMock = mock(DocumentSnapshot.class);

        // Define test data for the mock DocumentSnapshot
        String commentId = "comment123";
        String publisherId = "user123";
        String content = "Sample comment";
        String parentId = "post123";
        Date timestamp = new Date();

        // Stub the behavior of the mock DocumentSnapshot to return test data when its methods are called
        when(documentSnapshotMock.getId()).thenReturn(commentId);
        when(documentSnapshotMock.getString(Comment.PUBLISHER_ID)).thenReturn(publisherId);
        when(documentSnapshotMock.getString(Comment.CONTENT)).thenReturn(content);
        when(documentSnapshotMock.getString(Comment.PARENT_ID)).thenReturn(parentId);
        when(documentSnapshotMock.getTimestamp(Comment.TIMESTAMP)).thenReturn(new Timestamp(timestamp));

        // Call the deserialize method
        Comment comment = commentManager.deserialize(documentSnapshotMock);

        // Verify that the Comment object was created with the correct attributes
        assertEquals(comment.getCommentId(), commentId);
        assertEquals(comment.getPublisherId(), publisherId);
        assertEquals(comment.getText(), content);
        assertEquals(comment.getParentId(), parentId);
        assertEquals(comment.getDatetime(), timestamp);
    }

    @Test
    public void testSerialize() {
        // Define test data for a Comment object
        String commentId = "comment123";
        String publisherId = "user123";
        String content = "Sample comment";
        String parentId = "post123";
        Date timestamp = new Date();

        Comment comment = new Comment(commentId, publisherId, content, parentId, timestamp);

        // Call the serialize method
        Map<String, Object> serializedData = commentManager.serialize(comment);

        // Verify that the serialized data contains the correct attributes
        assertEquals(serializedData.get(Comment.PUBLISHER_ID), publisherId);
        assertEquals(serializedData.get(Comment.CONTENT), content);
        assertEquals(serializedData.get(Comment.PARENT_ID), parentId);
        assertEquals(((Timestamp) serializedData.get(Comment.TIMESTAMP)).toDate(), timestamp);
    }
}
