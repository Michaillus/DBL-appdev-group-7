package com.example.connectue.activities;

import androidx.annotation.NonNull;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.connectue.fragments.MaterialsFragment;
import com.example.connectue.R;
import com.example.connectue.fragments.ReviewsFragment;
import com.example.connectue.fragments.QuestionsFragment;
import com.example.connectue.databinding.ActivityCourseViewBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigationrail.NavigationRailView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity that shows a course and it's reviews, questions, materials.
 */
public class CourseViewActivity extends StudyUnitViewActivity {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "CourseViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int orientation = getResources().getConfiguration().orientation;
        // Check if saved instance state is not null

        setTabListener();
        // Set up UI elements
        loadStudyUnitDetails();




        ImageView followIcon = findViewById(R.id.followIcon);

        setFollowButtonListener(followIcon);

        setFollowIcon(followIcon);
    }



    protected void setBinding() {
        ActivityCourseViewBinding binding = ActivityCourseViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    protected void setTabListener() {
        TabLayout tabLayout = findViewById(R.id.tablayout_course_menu);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabText = tab.getText().toString();
                switch (tabText) {
                    case "Reviews":
                        Log.i(TAG, "Transferring to reviews tab");
                        replaceFragment(new ReviewsFragment());
                        break;
                    case "Questions":
                        Log.i(TAG, "Transferring to questions tag");
                        replaceFragment(new QuestionsFragment());
                        break;
                    case "Material":
                        Log.i(TAG, "Transferring to materials tab");
                        replaceFragment(new MaterialsFragment());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setRailListener() {
        NavigationRailView navigationRailView = findViewById(R.id.navigation_rail);
        navigationRailView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_reviews) {
                replaceFragment(new ReviewsFragment());
                return true;
            } else if (item.getItemId() == R.id.nav_questions) {
                replaceFragment(new QuestionsFragment());
                return true;
            } else if (item.getItemId() == R.id.nav_materials) {
                replaceFragment(new MaterialsFragment());
                return true;
            } else {
                replaceFragment(new ReviewsFragment());
                return true;
            }
        });

    }

    protected void setFollowButtonListener(ImageView followIcon) {

        LinearLayout followButton = findViewById(R.id.followButton);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                db.collection("users")
                    .document(firebaseUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    List<String> userCourses = (List<String>) documentSnapshot.get("userCourses");
                                    if (userCourses.contains(studyUnit.getId())) {
                                        userCourses.remove(studyUnit.getId());
                                    } else {
                                        userCourses.add(studyUnit.getId());
                                    }
                                    Map<String, Object> newList = new HashMap<>();
                                    newList.put("userCourses", userCourses);

                                    db.collection("users").document(firebaseUser.getUid()).update(newList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            if (userCourses.contains(studyUnit.getId())) {
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

    }

    protected void setFollowIcon(ImageView followIcon) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("users")
            .document(firebaseUser.getUid())
            .get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            List<String> userCourses = (List<String>) documentSnapshot.get("userCourses");
                            if (userCourses.contains(studyUnit.getId())) {
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

    }

}