package com.example.connectue.model;

import com.example.connectue.model.FullUser;

import java.util.ArrayList;

public class StudentUser extends FullUser {
    public StudentUser(String uid, String email, String password, String firstName, Boolean status, String lastName, String program, int role) {
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
