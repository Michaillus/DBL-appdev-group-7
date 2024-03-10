package com.example.connectue;

public class General {

    //    user role parameters in database
    public static final long ADMIN = 0;
    public static final long STUDENT = 1;
    public static final long GUEST = 2;

    //    the name of user collection, and the field names within
    public static final String USERCOLLECTION = "users";
    public static final String EMAIL = "email";
    public static final String FIRSTNAME = "firstName";
    public static final String VERIFY = "isVerified";
    public static final String LASTNAME = "lastName";
    public static final String PASSWORD = "password";
    public static final String POSTHISTORY = "postHistory";
    public static final String PROFILEPICTURE = "profilePicURL";
    public static final String PROGRAM = "program";
    public static final String REVIEWHISTORY = "reviewHistory";
    public static final String ROLE = "role";
    public static final String COURSE = "userCourses";
    public static final String USERID = "userId";
    public static final String PHONE = "phone";

    public static boolean isAdmin(long role) {
        return role == ADMIN;
    }

    public static boolean isStudent(long role) {
        return role == STUDENT;
    }

    public static boolean isGuest(long role) {
        return role != ADMIN && role!= STUDENT;
    }
}
