package com.example.connectue.model;

import com.example.connectue.model.User;

import java.util.List;

public abstract class FullUser extends User {
    public String program;
    public List<String> userCourses;
    public List<String> postHistory;
    public String profilePicURL;
    public List<String> reviewHistory;

}
