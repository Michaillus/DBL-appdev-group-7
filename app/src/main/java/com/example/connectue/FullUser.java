package com.example.connectue;

import java.util.List;

public class FullUser extends User{
    public String program;
    public Boolean isVerified = false;
    public List<Course> userCourses;
    public FullUser(String uid, String email, String password, String firstName, String lastName, String program) {
        this.userId = uid;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.program = program;
    }

    public void updateVerificationStatus(Boolean status) {
        isVerified = status;
    }
}
