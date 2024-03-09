package com.example.connectue;

public class General {
    public static final int ADMIN = 0;
    public static final int STUDENT = 1;
    public static final int GUEST = 2;

    public static boolean isAdmin(int role) {
        return role == ADMIN;
    }

    public static boolean isStudent(int role) {
        return role == STUDENT;
    }

    public static boolean isGuest(int role) {
        return role != ADMIN && role!= STUDENT;
    }
}
