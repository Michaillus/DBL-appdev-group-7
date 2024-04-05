package com.example.connectue.model;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Date;

public abstract class InteractableTest {

    protected Interactable interactable;

    String id = "2857395738754";
    String publisherId = "295782425868";
    String text = "Hello there!";
    long likeNumber = 47648;
    long dislikeNumber = 3952;
    long commentNumber = 57392;
    Date dateTime = new Date();

    @Test
    public void getId() {
        String result = interactable.getId();
        String expected = id;
        assertEquals(result, expected);
    }

    @Test
    public void setId() {
        String expected = "newId2954729572985";
        interactable.setId(expected);
        String result = interactable.getId();
        assertEquals(result, expected);
    }

    @Test
    public void getPublisherId() {
        String result = interactable.getPublisherId();
        String expected = publisherId;
        assertEquals(result, expected);
    }

    @Test
    public void setPublisherId() {
        String expected = "2598275783579";
        interactable.setPublisherId(expected);
        String result = interactable.getPublisherId();
        assertEquals(result, expected);
    }

    @Test
    public void getText() {
        String result = interactable.getText();
        String expected = text;
        assertEquals(result, expected);
    }

    @Test
    public void setText() {
        String expected = "bye bye";
        interactable.setText(expected);
        String result = interactable.getText();
        assertEquals(result, expected);
    }

    @Test
    public void getLikeNumber() {
        long result = interactable.getLikeNumber();
        long expected = likeNumber;
        assertEquals(result, expected);
    }

    @Test
    public void setLikeNumber() {
        long expected = 41;
        interactable.setLikeNumber(expected);
        long result = interactable.getLikeNumber();
        assertEquals(result, expected);
    }

    @Test
    public void incrementLikeNumber() {
        long expected = interactable.getLikeNumber() + 1;
        interactable.incrementLikeNumber();
        long result = interactable.getLikeNumber();
        assertEquals(result, expected);
    }

    @Test
    public void decrementLikeNumber() {
        long expected = interactable.getLikeNumber() - 1;
        interactable.decrementLikeNumber();
        long result = interactable.getLikeNumber();
        assertEquals(result, expected);
    }

    @Test
    public void getDislikeNumber() {
        long result = interactable.getDislikeNumber();
        long expected = dislikeNumber;
        assertEquals(result, expected);
    }

    @Test
    public void setDislikeNumber() {
        long expected = 37;
        interactable.setDislikeNumber(expected);
        long result = interactable.getDislikeNumber();
        assertEquals(result, expected);
    }

    @Test
    public void incrementDislikeNumber() {
        long expected = interactable.getDislikeNumber() + 1;
        interactable.incrementDislikeNumber();
        long result = interactable.getDislikeNumber();
        assertEquals(result, expected);
    }

    @Test
    public void decrementDislikeNumber() {
        long expected = interactable.getDislikeNumber() - 1;
        interactable.decrementDislikeNumber();
        long result = interactable.getDislikeNumber();
        assertEquals(result, expected);
    }

    @Test
    public void getCommentNumber() {
        long result = interactable.getCommentNumber();
        long expected = commentNumber;
        assertEquals(result, expected);
    }

    @Test
    public void setCommentNumber() {
        long expected = 2957;
        interactable.setCommentNumber(expected);
        long result = interactable.getCommentNumber();
        assertEquals(result, expected);
    }

    @Test
    public void incrementCommentNumber() {
        long expected = interactable.getCommentNumber() + 1;
        interactable.incrementCommentNumber();
        long result = interactable.getCommentNumber();
        assertEquals(result, expected);
    }

    @Test
    public void decrementCommentNumber() {
        long expected = interactable.getCommentNumber() - 1;
        interactable.decrementCommentNumber();
        long result = interactable.getCommentNumber();
        assertEquals(result, expected);
    }

    @Test
    public void getDatetime() {
        Date result = interactable.getDatetime();
        Date expected = dateTime;
        assertEquals(result, expected);
    }

    @Test
    public void setDatetime() {
        Date expected = new Date();
        interactable.setDatetime(expected);
        Date result = interactable.getDatetime();
        assertEquals(result, expected);
    }

    @Test
    public void constructorPublisherIdNullExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> interactable =
                new Interactable(id, null, text, likeNumber, dislikeNumber,
                commentNumber, dateTime) {});
    }

    @Test
    public void constructorTextNullExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> interactable =
                new Interactable(id, publisherId, null, likeNumber, dislikeNumber,
                        commentNumber, dateTime) {});
    }

    @Test
    public void constructorLikeNumberNullExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> interactable =
                new Interactable(id, publisherId, text, null, dislikeNumber,
                        commentNumber, dateTime) {});
    }

    @Test
    public void constructorDislikeNumberExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> interactable =
                new Interactable(id, publisherId, text, likeNumber, null,
                        commentNumber, dateTime) {});
    }

    @Test
    public void constructorCommentNumberNullExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> interactable =
                new Interactable(id, publisherId, text, likeNumber, dislikeNumber,
                        null, dateTime) {});
    }
}