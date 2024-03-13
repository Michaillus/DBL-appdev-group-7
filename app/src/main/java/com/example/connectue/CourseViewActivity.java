package com.example.connectue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseViewActivity extends AppCompatActivity {

    private String courseId = "";
    private String TAG = "CourseViewUtil";
    FirebaseFirestore db;

    TextView title;
    RatingBar ratingBar;
    TextView ratingIndicator;
    LinearLayout followButton;
    FirebaseUser user;
    Float averageRating = 0f;
    Course course;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_view);
        ratingBar = findViewById(R.id.ratingBar);
        ratingIndicator = findViewById(R.id.rating);
        followButton = findViewById(R.id.followButton);
        db = FirebaseFirestore.getInstance();

        title = findViewById(R.id.titleCourse);

        ImageView followIcon = findViewById(R.id.followIcon);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                courseId= "";
            } else {
                courseId= extras.getString("courseId");
            }
        } else {
            courseId= (String) savedInstanceState.getSerializable("courseId");
        }

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    List<String> userCourses = (List<String>) documentSnapshot.get("userCourses");
                                    if (userCourses.contains(courseId)) {
                                        followIcon.setImageDrawable(getResources().getDrawable(R.drawable.baseline_check_circle_24));
                                    } else {
                                        followIcon.setImageDrawable(getResources().getDrawable(R.drawable.baseline_add_circle_outline_24));
                                    }
                                } else {
                                    Log.d(TAG, "Couldn't fetch document");
                                }
                            }
                    }
                });

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, user.getUid().toString());
                db.collection("users")
                        .document(user.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                List<String> userCourses = (List<String>) documentSnapshot.get("userCourses");
                                if (userCourses.contains(courseId)) {
                                    userCourses.remove(courseId);
                                } else {
                                    userCourses.add(courseId);
                                }
                                Map<String, Object> newList = new HashMap<>();
                                newList.put("userCourses", userCourses);

                                db.collection("users").document(user.getUid()).update(newList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        if (userCourses.contains(courseId)) {
                                            followIcon.setImageDrawable(getResources().getDrawable(R.drawable.baseline_check_circle_24));
                                            Toast.makeText(CourseViewActivity.this, "Course followed", Toast.LENGTH_SHORT).show();
                                        } else {
                                            followIcon.setImageDrawable(getResources().getDrawable(R.drawable.baseline_add_circle_outline_24));
                                            Toast.makeText(CourseViewActivity.this, "Course unfollowed", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "Couldn't get document data");
                        }


                    }
                });
            }
        });

        Log.d(TAG, courseId);
        loadCourseDetails();


    }

    private void loadCourseDetails() {
        db.collection("courses").document(courseId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String courseCode = (String) document.get("courseCode");
                        String courseName = (String) document.get("courseName");
                        List<Long> ratings = (List<Long>) document.get("courseRating");

                        course = new Course(courseName, courseCode, courseId);
                        int count = 0;

                        for (Long ratingValue : ratings) {
                            Log.d(TAG, ratingValue.toString());
                            int intValue = ratingValue.intValue(); // Convert Long to int
                            course.addRating(intValue);
                            averageRating += ratingValue.floatValue();
                            count += 1;
                            Log.d(TAG, "Rating: " + intValue);
                        }

                        averageRating /= count;

                    } else {
                        Log.d(TAG, "Course doesn't exist");
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

                ratingBar.setIsIndicator(true);
                ratingBar.setRating(averageRating);
                ratingIndicator.setText(averageRating.toString() + "/5");
                title.setText(course.getCourseCode());
                title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            }
        });
    }
}