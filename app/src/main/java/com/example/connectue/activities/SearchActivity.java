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
import com.example.connectue.model.Course;
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

public class SearchActivity extends AppCompatActivity {
    // private String info; // Retrieve the content of the input field
    private TextView foundCourse;
    private CardView courseCardView;
    private TextView courseTextView;
    FirebaseFirestore db;
    CollectionReference coursesRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        foundCourse = findViewById(R.id.foundCourse);
        courseCardView = findViewById(R.id.courseCard);
        courseTextView = findViewById(R.id.courseCardText);

        foundCourse = findViewById(R.id.foundCourse);
        db = FirebaseFirestore.getInstance();
        coursesRef = db.collection("courses");

        // get user input search text
        String searchText = getIntent().getStringExtra("searchText");

        searchCourse(searchText);

    }

    private void searchCourse(String searchText) {
        String searchTextUpperCase = searchText.toUpperCase();

        Query queryByCode = coursesRef.whereEqualTo("courseCode", searchTextUpperCase);
        Query queryByName = coursesRef.whereEqualTo("courseName", searchText);

        Tasks.whenAllSuccess(queryByCode.get(), queryByName.get())
                .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> results) {
                        List<Course> foundCourses = new ArrayList<>();

                        // deal with the results
                        for (Object result : results) {
                            if (result instanceof QuerySnapshot) {
                                QuerySnapshot querySnapshot = (QuerySnapshot) result;
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    String courseCode = document.getString("courseCode");
                                    String courseName = document.getString("courseName");
                                    long ratingSum = document.getLong("ratingSum");
                                    long ratingNumber = document.getLong("ratingNumber");

                                    foundCourses.add(new Course(document.getId(), courseName,
                                            courseCode, ratingSum, ratingNumber,
                                            Course.StudyUnitType.COURSE));
                                    // replace the text in card view by course code of the found course
                                    courseTextView.setText(courseCode);
                                }
                            }
                        }

                        // show the result in TextView
                        if (!foundCourses.isEmpty()) {
                            foundCourse.setText("Found Courses:");

                            // By clicking the card view, go to the course review page
                            courseCardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Start new activity and pass course id
                                    Intent intent = new Intent(SearchActivity.this, CourseViewActivity.class);
                                    intent.putExtra("courseId", foundCourses.get(0).getId()); // Assuming getId() returns the id of the course
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
