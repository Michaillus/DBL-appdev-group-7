package com.example.connectue;

import static org.junit.Assert.assertEquals;

import com.example.connectue.model.Post;
import com.example.connectue.utils.General;
import com.example.connectue.utils.TimeUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UtilsUnitTests {
    //////////////////////////////////////////////////////////////////
    //                      General.java tests                      //
    //////////////////////////////////////////////////////////////////

    /**
     * Test method toStringList.
     * Motivation: test typical behaviour
     */
    @Test
    public void toStringList_TypicalBehaviour() {
        Post post = new Post("0", "some text", "some url");
        List<String> list1 = new ArrayList<>();
        list1.add(post.toString());
        System.out.println(General.toStringList(post));
        List<String> list2 = new ArrayList<>();
        list2.add(post.toString());

        assertEquals(list2, General.toStringList(list1));
    }

    /**
     * Test method toStringList.
     * Motivation: test typical behaviour when input is not instanceof list.
     */
    @Test
    public void toStringList_TypicalBehaviourForAlternativeInput() {
        Post post = new Post("0", "some text", "some url");
        System.out.println(General.toStringList(post));
        List<String> list2 = new ArrayList<>();

        assertEquals(list2, General.toStringList(post));
    }

    //////////////////////////////////////////////////////////////////
    //                      TimeUtils.java tests                    //
    //////////////////////////////////////////////////////////////////
    /**
     * Test method getTimeAgo.
     * Motivation: test typical behaviour.
     */
    @Test
    public void getTimeAgo_TypicalBehaviour() {
        assertEquals("just now", TimeUtils.getTimeAgo(new Date()));
    }


}
