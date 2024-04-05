package com.example.connectue.model;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test class for review model.
 */
public class ReviewTest extends InteractableTest {

    Review review;

    long stars = 3;

    String parentId = "957253852793";

    public ReviewTest() {
        review = new Review(id, publisherId, text, stars, likeNumber, dislikeNumber, commentNumber,
                dateTime, parentId);
        interactable = review;
    }

    /**
     * Test stars getter.
     */
    @Test
    public void getStars() {
        long result = review.getStars();
        long expected = stars;
        assertEquals(result, expected);
    }

    /**
     * Test stars setter.
     */
    @Test
    public void setStars() {
        long expected = 1;
        review.setStars(expected);
        long result = review.getStars();
        assertEquals(result, expected);
    }

    /**
     * Test parent id getter.
     */
    @Test
    public void getParentId() {
        String result = review.getParentId();
        String expected = parentId;
        assertEquals(result, expected);
    }

    /**
     * Test parent id setter.
     */
    @Test
    public void setParentId() {
        String expected = "3674856484625444";
        review.setParentId(expected);
        String result = review.getParentId();
        assertEquals(result, expected);
    }
}