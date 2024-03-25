package com.example.connectue.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.connectue.R;
import com.example.connectue.activities.CourseViewActivity;
import com.example.connectue.model.Course;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCoursesVerticalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCoursesVerticalFragment extends Fragment {

    FirebaseUser user;
    FirebaseFirestore db;

    List<Course> userCourses;
    Object courseName;
    Object courseCode;

    Object ratingObject;
    private String TAG = "MyCoursesVerticalFragUtil: ";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyCoursesVerticalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyCoursesVerticalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyCoursesVerticalFragment newInstance(String param1, String param2) {
        MyCoursesVerticalFragment fragment = new MyCoursesVerticalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_courses_vertical, container, false);
        LinearLayout scrollViewLayout = view.findViewById(R.id.myCoursesScrollViewLayout);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        userCourses = new ArrayList<>();

        DocumentReference userDoc = db.collection("users").document(user.getUid());

        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve the value of userCourses
                        List<String> userCoursesObj = (List<String>) document.get("userCourses");
                        Log.d(TAG, userCoursesObj.toString());
                        if (userCoursesObj != null && userCoursesObj.size() != 0) {
                            for (String courseId: userCoursesObj) {
                                Log.d(TAG, courseId);
                                db.collection("courses").document(courseId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if (documentSnapshot.exists()) {
                                                courseName = documentSnapshot.get("courseName");
                                                courseCode = documentSnapshot.get("courseCode");
                                                ratingObject = documentSnapshot.get("courseRating");
                                                Course course = new Course(courseName.toString(), courseCode.toString(), courseId);
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
                                                userCourses.add(course);
                                                Log.d(TAG, course.getCourseCode());
                                                //Remember: inflater is used to instantiate layout XML files into their
                                                //corresponding View objects in the app's user interface.
                                                View cardView = inflater.inflate(R.layout.clickable_course, null);


                                                TextView textView = cardView.findViewById(R.id.courseCardText);

                                                //give layout parameters to each course card.
                                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                        LinearLayout.LayoutParams.MATCH_PARENT
                                                );
                                                layoutParams.bottomMargin = 35;
                                                textView.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                                                textView.setText(course.getCourseCode());
                                                cardView.setLayoutParams(layoutParams);
                                                scrollViewLayout.addView(cardView);
                                                cardView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        // Start new activity and pass course id
                                                        Intent intent = new Intent(getActivity(), CourseViewActivity.class);
                                                        intent.putExtra("courseId", course.getCourseId()); // Assuming getId() returns the id of the course
                                                        startActivity(intent);
                                                    }
                                                });
                                                Log.d(TAG, userCourses.toString());
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                        Log.d(TAG, "Field Value: " + userCoursesObj.toString());
                                    }
                                });
                            }

                        } else {
                            Log.d(TAG, "Field does not exist");
                            TextView noCoursesTextView = new TextView(getContext());
                            noCoursesTextView.setText("You have no courses, you can add some by hitting the follow button.");
                            scrollViewLayout.addView(noCoursesTextView);
                        }
                    } else {
                        Log.d(TAG, "Document does not exist");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }


            }
        });

        return view;


    }
}