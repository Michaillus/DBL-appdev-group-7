package com.example.connectue;

public class StudentUser extends FullUser {
    public StudentUser(String uid, String email, String password, String firstName, Boolean status, String lastName, String program) {
        this.userId = uid;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.program = program;
        this.isVerified = status;
    }

}
