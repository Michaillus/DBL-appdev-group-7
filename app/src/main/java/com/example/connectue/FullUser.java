package com.example.connectue;

import java.util.List;

public abstract class FullUser extends User {
    public String program;
    public Boolean isVerified;
    public List<Course> userCourses;
}
