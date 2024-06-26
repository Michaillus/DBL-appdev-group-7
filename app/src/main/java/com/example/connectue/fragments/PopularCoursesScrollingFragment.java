package com.example.connectue.fragments;

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
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.managers.StudyUnitManager;
import com.example.connectue.model.StudyUnit;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Class to manage display of horizontal scroll fragment in courses tab of channels view.
 */
public class PopularCoursesScrollingFragment extends Fragment {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "PopularCoursesScrollingFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular_courses_scrolling, container, false);
        LinearLayout scrollViewLayout = view.findViewById(R.id.scrollViewLayout);

        StudyUnitManager courseManager = new StudyUnitManager(FirebaseFirestore.getInstance(),
                StudyUnit.COURSE_COLLECTION_NAME);
        //Get all the courses from the database.
        courseManager.downloadAll(new ItemDownloadCallback<List<StudyUnit>>() {
            @Override
            public void onSuccess(List<StudyUnit> courses) {
                Log.i(TAG, "Courses are retrieved successfully");
                //add fetched courses to list of Course observers
                for (StudyUnit course : courses) {
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

                    //Set the title of each course card based on the retrieved
                    //daa and set styles for clearer indication.
                    layoutParams.rightMargin = 35;
                    textView.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    textView.setText(course.getCode());
                    cardView.setLayoutParams(layoutParams);


                    //set a listener for the card view.
                    //When a card is clicked, direct to user to a new
                    //course view activity.
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Start new activity and pass course id
                            Intent intent = new Intent(getActivity(), CourseViewActivity.class);
                            intent.putExtra("course", course.studyUnitToString());
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        return view;

    }
}