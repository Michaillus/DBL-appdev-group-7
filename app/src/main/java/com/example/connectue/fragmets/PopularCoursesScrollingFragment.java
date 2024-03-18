package com.example.connectue.fragmets;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.connectue.R;
import com.example.connectue.activities.CourseViewActivity;
import com.example.connectue.model.Course;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to manage display of horizontal scroll fragment in courses tab of channels view.
 */
public class PopularCoursesScrollingFragment extends Fragment {
    FirebaseUser user;
    FirebaseFirestore db;

    List<Course> courses;

    Object courseName;
    Object courseCode;

    Object ratingObject;
    private String TAG = "PopCoursesFragUtil: ";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular_courses_scrolling, container, false);
        LinearLayout scrollViewLayout = view.findViewById(R.id.scrollViewLayout);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        courses = new ArrayList<>();

        db.collection("courses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    //onComplete is an asynchronous method, therefore it is needed to
                    //finish all tasks requiring the fetched objects within the method.
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                courseName = document.get("courseName");
                                courseCode = document.get("courseCode");
                                ratingObject = document.get("courseRating");
                                Course course = new Course(courseName.toString(), courseCode.toString(), document.getId());
                                if (ratingObject instanceof List<?>) {
                                    //Number value from json is converted into Long through unbounded wild card "?"
                                    List<Long> ratingList = (List<Long>) ratingObject;
                                    for (Long ratingValue : ratingList) {
                                        int intValue = ratingValue.intValue(); // Convert Long to int
                                        course.addRating(intValue);
                                        Log.d(TAG, "Rating: " + intValue);
                                        // Add rating processing logic here
                                    }
                                } else {
                                    Log.d(TAG, "Rating is not a list");
                                    // Handle the case where the rating is not a list
                                }

                                courses.add(course);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        //add fetched courses to list of Course observers
                        for (Course course : courses) {
                            //Remember: inflater is used to instantiate layout XML files into their
                            //corresponding View objects in the app's user interface.
                            View cardView = inflater.inflate(R.layout.clickable_course, null);
                            scrollViewLayout.addView(cardView);

                            TextView textView = cardView.findViewById(R.id.courseCardText);

                            //give layout parameters to each course card.
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    250,
                                    LinearLayout.LayoutParams.MATCH_PARENT
                            );
                            layoutParams.rightMargin = 35;
                            textView.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                            textView.setText(course.getCourseCode());
                            cardView.setLayoutParams(layoutParams);

                            cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Start new activity and pass course id
                                    Intent intent = new Intent(getActivity(), CourseViewActivity.class);
                                    intent.putExtra("courseId", course.getCourseId()); // Assuming getId() returns the id of the course
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });


        return view;
    }
}