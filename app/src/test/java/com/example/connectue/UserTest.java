package com.example.connectue;

import com.example.connectue.model.User;

import org.junit.Test;
import static org.junit.Assert.*;

public class UserTest {
    protected User user;

    String id = "myUserId105758785";
    String firstName = "Joe";
    String lastName = "Biden";
    boolean isVerified = true;
    String email = "randomguy@gmail.com";
    String phoneNumber = "+312852957295";
    String profilePicUrl = "someUrl";
    String program = "BCSE";
    int role = 1;

    public void setUp() {
        user = new User(id, firstName, lastName, isVerified, email, phoneNumber, profilePicUrl, program, role);
    }

    @Test
    public void testGetId() {
        setUp();
        String result = user.getId();
        String expected = id;
        assertEquals(result, expected);
    }

    @Test
    public void testSetId() {
        setUp();
        String expected = "newId2954729572985";
        user.setId(expected);
        String result = user.getId();
        assertEquals(result, expected);
    }
}
