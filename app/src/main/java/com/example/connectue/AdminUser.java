package com.example.connectue;

import java.util.ArrayList;

public class AdminUser extends FullUser{
    public AdminUser(String uid, String email, String password, String firstName, Boolean status, String lastName, String program, int role) {
        this.userId = uid;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.program = program;
        this.isVerified = status;
        this.role = role;
        this.userCourses = new ArrayList<>();
        this.reviewHistory = new ArrayList<>();
        this.postHistory = new ArrayList<>();
        this.profilePicURL = "";
    }
}
