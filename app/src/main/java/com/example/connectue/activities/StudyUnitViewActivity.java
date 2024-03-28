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
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Abstract activity for showing the data (reviews, questions, materials, ratings)
 * connected to a study unit - major or course.
 */
public abstract class StudyUnitViewActivity extends AppCompatActivity {

    /**
     * Instance of a study unit.
     */
    protected StudyUnit studyUnit;

    /**
     * Class tag for logs.
     */
    private static final String TAG = "StudyUnitViewActivity";

    /**
     * Instance of a FireStore database.
     */
    protected FirebaseFirestore db;

    protected TextView title;

    protected RatingBar ratingBar;

    protected TextView ratingIndicator;

    protected LinearLayout followButton;

    protected ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBinding();

        replaceFragment(new ReviewsFragment());

        ratingBar = findViewById(R.id.studyUnitRating);
        ratingIndicator = findViewById(R.id.rating);
        followButton = findViewById(R.id.followButton);
        db = FirebaseFirestore.getInstance();

        title = findViewById(R.id.studyUnitTitle);

        backButton = findViewById(R.id.back_btn);

        studyUnit = ActivityUtils.getCourse(this, savedInstanceState);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadStudyUnitDetails();


    }

    protected abstract void setBinding();

    protected void loadStudyUnitDetails() {
        if (studyUnit.getId() == null || studyUnit.getId().equals("0")) {
            Log.e(TAG, "Null study unit");
        }

        ratingBar.setIsIndicator(true);
        // Set stars to the average rating
        ratingBar.setRating(studyUnit.getAverageRating());
        // Set average rating
        ratingIndicator.setText(String.format("%.1f", studyUnit.getAverageRating()) + " / 5");
        // Set code of the study unit as a title
        title.setText(studyUnit.getCode());
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

    }

    protected void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Getter for the study unit of the activity.
     * @return Study unit.
     */
    public StudyUnit getStudyUnit() {
        return studyUnit;
    }
}