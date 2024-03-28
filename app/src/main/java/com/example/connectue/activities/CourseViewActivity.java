package com.example.connectue.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connectue.fragments.MaterialsFragment;
import com.example.connectue.R;
import com.example.connectue.fragments.ReviewsFragment;
import com.example.connectue.fragments.QuestionsFragment;
import com.example.connectue.databinding.ActivityCourseViewBinding;
import com.example.connectue.model.Course;
import com.example.connectue.utils.ActivityUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseViewActivity extends AppCompatActivity {

    private Course course;

    private static final String TAG = "CourseViewActivity";
    FirebaseFirestore db;

    TextView title;
    RatingBar ratingBar;
    TextView ratingIndicator;
    LinearLayout followButton;
    ImageView backbtn;
    FirebaseUser user;

    ActivityCourseViewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourseViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new ReviewsFragment());
        TabLayout tabLayout = findViewById(R.id.tablayout_course_menu);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
               @Override
               public void onTabSelected(TabLayout.Tab tab) {
                   String tabText = tab.getText().toString();
                   if (tabText.equals("Reviews")) {
                       Log.d(TAG, "reviews");
                       replaceFragment(new ReviewsFragment());
                   } else if (tabText.equals("Questions")) {
                       Log.d(TAG, "questions");
                       replaceFragment(new QuestionsFragment());
                   } else if (tabText.equals("Material")) {
                       Log.d(TAG, "materials");
                       replaceFragment(new MaterialsFragment());
                   }
               }

               @Override
               public void onTabUnselected(TabLayout.Tab tab) {

               }

               @Override
               public void onTabReselected(TabLayout.Tab tab) {

               }
           });

        ratingBar = findViewById(R.id.courseRating);
        ratingIndicator = findViewById(R.id.rating);
        followButton = findViewById(R.id.followButton);
        db = FirebaseFirestore.getInstance();

        title = findViewById(R.id.titleCourse);

        ImageView followIcon = findViewById(R.id.followIcon);
        backbtn = findViewById(R.id.back_btn);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        course = ActivityUtils.getCourse(this, savedInstanceState);


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
                                    if (userCourses.contains(course.getId())) {
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
                                if (userCourses.contains(course.getId())) {
                                    userCourses.remove(course.getId());
                                } else {
                                    userCourses.add(course.getId());
                                }
                                Map<String, Object> newList = new HashMap<>();
                                newList.put("userCourses", userCourses);

                                db.collection("users").document(user.getUid()).update(newList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        if (userCourses.contains(course.getId())) {
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

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadCourseDetails();


    }

    private void loadCourseDetails() {
        if (course.getId() == null || course.getId().equals("0")) {
            Log.e(TAG, "Null course");
        }

        ratingBar.setIsIndicator(true);
        // Set stars to the average rating
        ratingBar.setRating(course.getAverageRating());
        // Set average rating
        ratingIndicator.setText(String.format("%.1f", course.getAverageRating()) + " / 5");
        // Set course code
        title.setText(course.getCourseCode());
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public Course getCourse() {
        return course;
    }
}