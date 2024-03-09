package com.example.connectue;

import java.util.ArrayList;
import java.util.List;

public class Course {
    public String courseName;
    public String courseCode;

    public List<Integer> rating;

    public Course(String courseName, String courseCode) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.rating = new ArrayList<>();
    }

    public void addRating(int rating) {
        this.rating.add(rating);
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }
}
