package com.example.connectue.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.connectue.R;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.managers.StudyUnitManager;
import com.example.connectue.model.StudyUnit;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * The user can search the course using course name or course code.
 */
public class SearchActivity extends AppCompatActivity {
    // Declare UI elements
    private TextView foundCourse;
    private CardView courseCardView;
    private TextView courseTextView;
    FirebaseFirestore db;
    CollectionReference coursesRef;

    /**
     * Initialize the activity when it is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize UI elements
        foundCourse = findViewById(R.id.foundCourse);
        courseCardView = findViewById(R.id.courseCard);
        courseTextView = findViewById(R.id.courseCardText);

        // Initialize Firestore database and collection reference
        foundCourse = findViewById(R.id.foundCourse);
        db = FirebaseFirestore.getInstance();
        coursesRef = db.collection(StudyUnit.COURSE_COLLECTION_NAME);

        // Retrieve search text from intent
        String searchText = getIntent().getStringExtra("searchText");

        searchCourse(searchText);

    }

    /**
     * Search the input course entered by the user in database.
     *
     * @param searchText input text in search bar
     */
    private void searchCourse(String searchText) {
        // Convert search text to uppercase for case-insensitive search
        String searchTextUpperCase = searchText.toUpperCase();
        // Define queries to search by code and name
        Query queryByCode = coursesRef.whereEqualTo(StudyUnit.CODE_ATTRIBUTE, searchTextUpperCase);
        Query queryByName = coursesRef.whereEqualTo(StudyUnit.NAME_ATTRIBUTE, searchText);

        Tasks.whenAllSuccess(queryByCode.get(), queryByName.get())
                .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> results) {
                        // List to store found courses
                        List<StudyUnit> foundCourses = new ArrayList<>();

                        StudyUnitManager courseManager = new StudyUnitManager(FirebaseFirestore.getInstance(),
                                StudyUnit.COURSE_COLLECTION_NAME);

                        // deal with the results
                        for (Object result : results) {
                            if (result instanceof QuerySnapshot) {
                                QuerySnapshot querySnapshot = (QuerySnapshot) result;
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    StudyUnit course = courseManager.deserialize(document);
                                    // Add found course to list
                                    foundCourses.add(course);
                                    // replace the text in card view by course code of the found course
                                    courseTextView.setText(course.getCode());
                                }
                            }
                        }

                        // Display search results
                        if (!foundCourses.isEmpty()) {
                            foundCourse.setText("Found Courses:");

                            // Set click listener for card view to view course details
                            courseCardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Start new activity and pass course id
                                    Intent intent = new Intent(SearchActivity.this, CourseViewActivity.class);
                                    intent.putExtra("course", foundCourses.get(0).studyUnitToString());
                                    startActivity(intent);
                                }
                            });

                        } else {
                            foundCourse.setText("No matching courses found.");
                            // if no course is found, set the card view invisible
                            courseCardView.setVisibility(View.INVISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // deal with failure
                        foundCourse.setText("Error searching for courses: " + e.getMessage());
                    }
                });
    }
}
