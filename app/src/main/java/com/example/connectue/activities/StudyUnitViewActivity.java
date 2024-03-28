package com.example.connectue.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.connectue.R;
import com.example.connectue.fragments.ReviewsFragment;
import com.example.connectue.model.StudyUnit;
import com.example.connectue.utils.ActivityUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class StudyUnitViewActivity extends AppCompatActivity {

    protected StudyUnit studyUnit;

    private static final String TAG = "StudyUnitViewActivity";

    protected FirebaseFirestore db;

    protected TextView title;

    protected RatingBar ratingBar;

    protected TextView ratingIndicator;

    protected LinearLayout followButton;

    protected ImageView backbtn;

    protected FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBinding();

        replaceFragment(new ReviewsFragment());

        ratingBar = findViewById(R.id.courseRating);
        ratingIndicator = findViewById(R.id.rating);
        followButton = findViewById(R.id.followButton);
        db = FirebaseFirestore.getInstance();

        title = findViewById(R.id.titleCourse);

        backbtn = findViewById(R.id.back_btn);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        studyUnit = ActivityUtils.getCourse(this, savedInstanceState);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadCourseDetails();


    }

    protected abstract void setBinding();

    protected void loadCourseDetails() {
        if (studyUnit.getId() == null || studyUnit.getId().equals("0")) {
            Log.e(TAG, "Null course");
        }

        ratingBar.setIsIndicator(true);
        // Set stars to the average rating
        ratingBar.setRating(studyUnit.getAverageRating());
        // Set average rating
        ratingIndicator.setText(String.format("%.1f", studyUnit.getAverageRating()) + " / 5");
        // Set course code
        title.setText(studyUnit.getCode());
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

    }

    protected void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public StudyUnit getCourse() {
        return studyUnit;
    }
}