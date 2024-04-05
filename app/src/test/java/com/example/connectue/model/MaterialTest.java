package com.example.connectue.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class MaterialTest extends InteractableTest{

    Material material;
    String parentCourseId = "12123123123";
    String docUrl = "12312";


    public MaterialTest() {
        material = new Material(id, publisherId, text, likeNumber, dislikeNumber, commentNumber,
                dateTime, parentCourseId, docUrl);
        interactable = material;
    }

    @Test
    public void testSecondConstructor() {
        Material material1 = new Material(publisherId, text, parentCourseId, docUrl);
        assertEquals(publisherId, material1.getPublisherId());
        assertEquals(text, material1.getText());
        assertEquals(parentCourseId, material1.getParentCourseId());
        assertEquals(docUrl, material1.getDocUrl());
    }

    @Test
    public void getParentCourseId() {
        String result = material.getParentCourseId();
        String expected = parentCourseId;
        assertEquals(result, expected);
    }

    @Test
    public void setParentCourseId() {
        String expected = "3674856484625444";
        material.setParentCourseId(expected);
        String result = material.getParentCourseId();
        assertEquals(result, expected);
    }

    @Test
    public void getDocUrl() {
        String result = material.getDocUrl();
        String expected = "12312";
        assertEquals(result, expected);
    }

    @Test
    public void setDocUrl() {
        String expected = "3674856484625444";
        material.setDocUrl(expected);
        String result = material.getDocUrl();
        assertEquals(result, expected);
    }
}