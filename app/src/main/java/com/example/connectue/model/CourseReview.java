package com.example.connectue.model;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.connectue.R;

public class CourseReview extends AppCompatActivity {


    private Button review_btn;
    private Button question_btn;
    private Button material_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_review);

        review_btn = findViewById(R.id.Review_btn);
        question_btn = findViewById(R.id.Question_btn);
        material_btn = findViewById(R.id.Material_btn);

        review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageToReviews();
            }
        });

        question_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageToQuestions();
            }
        });

        material_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageToMaterials();
            }
        });
    }

    public void pageToReviews() {

        //wait for the CourseReview class to directed to the page
        //seems like we had it... but later

        //Intent intent = new Intent(this, Reviews.class);
        //startActivity(intent);

    }

    public void pageToQuestions() {

        //wait for the Questions class to directed to the page

        //Intent intent = new Intent(this, Questions.class);
        //startActivity(intent);
    }

    public void pageToMaterials() {

        //same as above.
    }
}
