package com.example.connectue.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for user model.
 */
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

    /**
     * Set up user instance.
     */
    @Before
    public void setUp() {
        user = new User(id, firstName, lastName, isVerified, email, phoneNumber, profilePicUrl, program, role);
    }

    /**
     * Test id getter.
     */
    @Test
    public void testGetId() {
        String result = user.getId();
        String expected = id;
        assertEquals(result, expected);
    }

    /**
     * Test id setter.
     */
    @Test
    public void testSetId() {
        String expected = "newId2954729572985";
        user.setId(expected);
        String result = user.getId();
        assertEquals(result, expected);
    }

    /**
     * Test first name getter.
     */
    @Test
    public void testGetFirstName() {
        String result = user.getFirstName();
        String expected = firstName;
        assertEquals(result, expected);
    }

    /**
     * Test first name setter.
     */
    @Test
    public void testSetFirstName() {
        String expected = "Alex";
        user.setFirstName(expected);
        String result = user.getFirstName();
        assertEquals(result, expected);
    }

    /**
     * Test last name getter.
     */
    @Test
    public void testGetLastName() {
        String result = user.getLastName();
        String expected = lastName;
        assertEquals(result, expected);
    }

    /**
     * Test last name setter.
     */
    @Test
    public void testSetLastName() {
        String expected = "Smith";
        user.setLastName(expected);
        String result = user.getLastName();
        assertEquals(result, expected);
    }

    /**
     * Test full name getter.
     */
    @Test
    public void getFullName() {
        String result = user.getFullName();
        String expected = firstName + " " + lastName;
        assertEquals(result, expected);
    }

    /**
     * Test is verified getter.
     */
    @Test
    public void isVerified() {
        boolean result = user.isVerified();
        boolean expected = isVerified;
        assertEquals(result, expected);
    }

    /**
     * Test is verified setter.
     */
    @Test
    public void setIsVerified() {
        boolean expected = false;
        user.setIsVerified(expected);
        boolean result = user.isVerified();
        assertEquals(result, expected);
    }

    /**
     * Test email getter.
     */
    @Test
    public void getEmail() {
        String result = user.getEmail();
        String expected = email;
        assertEquals(result, expected);
    }

    /**
     * Test email setter.
     */
    @Test
    public void setEmail() {
        String expected = "a@gmail.com";
        user.setEmail(expected);
        String result = user.getEmail();
        assertEquals(result, expected);
    }

    /**
     * Test phone number getter.
     */
    @Test
    public void getPhoneNumber() {
        String result = user.getPhoneNumber();
        String expected = phoneNumber;
        assertEquals(result, expected);
    }

    /**
     * Test phone number setter.
     */
    @Test
    public void setPhoneNumber() {
        String expected = "+1563872976";
        user.setEmail(expected);
        String result = user.getEmail();
        assertEquals(result, expected);
    }

    /**
     * Test profile picture url getter.
     */
    @Test
    public void getProfilePicUrl() {
        String result = user.getProfilePicUrl();
        String expected = profilePicUrl;
        assertEquals(result, expected);
    }

    /**
     * Test profile picture url setter.
     */
    @Test
    public void setProfilePicUrl() {
        String expected = "url29579589527";
        user.setProfilePicUrl(expected);
        String result = user.getProfilePicUrl();
        assertEquals(result, expected);
    }

    /**
     * Test program getter.
     */
    @Test
    public void getProgram() {
        String result = user.getProgram();
        String expected = program;
        assertEquals(result, expected);
    }

    /**
     * Test program setter.
     */
    @Test
    public void setProgram() {
        String expected = "BAM";
        user.setProgram(expected);
        String result = user.getProgram();
        assertEquals(result, expected);
    }

    /**
     * Test role getter.
     */
    @Test
    public void getRole() {
        int result = user.getRole();
        int expected = role;
        assertEquals(result, expected);
    }

    /**
     * Test role setter.
     */
    @Test
    public void setRole() {
        int expected = 0;
        user.setRole(expected);
        int result = user.getRole();
        assertEquals(result, expected);
    }
}