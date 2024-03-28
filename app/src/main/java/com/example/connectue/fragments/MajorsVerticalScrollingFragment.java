package com.example.connectue.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.connectue.R;
import com.example.connectue.activities.CourseViewActivity;
import com.example.connectue.activities.MajorViewActivity;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.managers.CourseManager;
import com.example.connectue.model.Course;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MajorsVerticalScrollingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MajorsVerticalScrollingFragment extends Fragment {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "MajorsVerticalScrollingFragment";

    public MajorsVerticalScrollingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MajorsVerticalScrollingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MajorsVerticalScrollingFragment newInstance(String param1, String param2) {
        MajorsVerticalScrollingFragment fragment = new MajorsVerticalScrollingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_majors_vertical_scrolling, container, false);
        LinearLayout scrollViewLayout = view.findViewById(R.id.majorsScrollViewLayout);

        CourseManager majorManager = new CourseManager(FirebaseFirestore.getInstance(),
                Course.MAJOR_COLLECTION_NAME);

        majorManager.downloadAll(new ItemDownloadCallback<List<Course>>() {
            @Override
            public void onSuccess(List<Course> majors) {
                Log.i(TAG, "Majors are retrieved successfully");
                for (Course major : majors) {
                    //Remember: inflater is used to instantiate layout XML files into their
                    //corresponding View objects in the app's user interface.
                    View cardView = inflater.inflate(R.layout.clickable_course, null);
                    scrollViewLayout.addView(cardView);

                    TextView textView = cardView.findViewById(R.id.courseCardText);

                    //give layout parameters to each course card.
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    );
                    layoutParams.bottomMargin = 35;
                    textView.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    textView.setText(major.getName());
                    cardView.setLayoutParams(layoutParams);

                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Start new activity and pass course id
                            Intent intent = new Intent(getActivity(), MajorViewActivity.class);
                            intent.putExtra("course", major.courseToString());
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        return view;

    }
}