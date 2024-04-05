package com.example.connectue.activities;

import android.content.res.Configuration;
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
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.managers.StudyUnitManager;
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

    /**
     * Title of the study unit to
     * display to user
     */
    protected TextView title;

    /**
     * The element that contains the
     * rating indication of the study unit
     */
    protected RatingBar ratingBar;

    /**
     * Text that indicates numbered value of rating
     */
    protected TextView ratingIndicator;

    protected LinearLayout followButton;

    protected ImageView backButton;

    String courseCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.i(TAG, "saved instance");
            // Retrieve course code from saved instance state
            studyUnit = ActivityUtils.getStudyUnit(StudyUnitViewActivity.this, savedInstanceState);
        } else {
            Log.i(TAG, "intent");
            // Retrieve course code from intent extras
            courseCode = getIntent().getStringExtra("course");
            studyUnit = StudyUnit.stringToStudyUnit(courseCode);
        }
        Log.d(TAG, courseCode + "DWI");
//        studyUnit = ActivityUtils.getStudyUnit(this, savedInstanceState);
        Log.d(TAG, studyUnit.getCode());
        /**
         * The first fragment to display is the reviews fragment
         */
        replaceFragment(new ReviewsFragment());
        setBinding();

        /**
         * find xml elements related to the relevant attributes.
         */
        ratingBar = findViewById(R.id.studyUnitRating);
        ratingIndicator = findViewById(R.id.rating);
        followButton = findViewById(R.id.followButton);

        /**
         * initialize firestore connection.
         */
        db = FirebaseFirestore.getInstance();

        title = findViewById(R.id.studyUnitTitle);

        backButton = findViewById(R.id.back_btn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        loadStudyUnitDetails();


    }

    /**
     * Always save the courseCode in the instance
     * so that we can retrieve it even when the page is
     * reloaded.
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save course code to the saved instance state bundle
        outState.putString("course", courseCode);
    }

    protected abstract void setBinding();

    /**
     * helper function that loads details to load
     * into
     */
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

    /**
     * Helper function that replaces the active fragment
     * by a new one
     * @param fragment the new fragment to display
     */
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

    public void reload() {
        StudyUnitManager studyUnitManager = new StudyUnitManager(FirebaseFirestore.getInstance(),
                studyUnit.getStudyUnitCollectionName());
        studyUnitManager.downloadOne(studyUnit.getId(), new ItemDownloadCallback<StudyUnit>() {
            @Override
            public void onSuccess(StudyUnit unit) {
                studyUnit = unit;
                loadStudyUnitDetails();
                replaceFragment(new ReviewsFragment());
                Log.i(TAG, "StudyUnitViewActivity is reloaded successfully");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while retrieving study unit");
            }
        });
    }
}