package com.example.connectue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

public class NavigationBar {

    public static void addNavigationBarActivitySwitch(AppCompatActivity activity) {

        ImageView profile = activity.findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentProfile = new Intent(activity, ProfileActivity.class);
                activity.startActivity(intentProfile);
            }
        });

        ImageView add_post = activity.findViewById(R.id.add_post);
        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentProfile = new Intent(activity, AddPostActivity.class);
                activity.startActivity(intentProfile);
            }
        });

        ImageView posts = activity.findViewById(R.id.posts);
        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPosts = new Intent(activity, MainActivity.class);
                activity.startActivity(intentPosts);
            }
        });

        ImageView courses = activity.findViewById(R.id.courses);
        courses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCourses = new Intent(activity, CoursesActivity.class);
                activity.startActivity(intentCourses);
            }
        });
    }
}
