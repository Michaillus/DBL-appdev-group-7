package com.example.connectue;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MajorsVerticalScrollingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MajorsVerticalScrollingFragment extends Fragment {

    FirebaseUser user;
    FirebaseFirestore db;
    List<Program> majors;
    private String TAG = "MajorsFragUtil: ";

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

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        majors = new ArrayList<>();

        db.collection("programs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    //onComplete is an asynchronous method, therefore it is needed to
                    //finish all tasks requiring the fetched objects within the method.
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String programName = (String) document.get("programName");
                                Program program = new Program(programName.toString());
                                majors.add(program);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        //add fetched courses to list of Course observers
                        for (Program program : majors) {
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
                            textView.setText(program.getProgramName());
                            cardView.setLayoutParams(layoutParams);
                        }
                    }
                });

        return view;


    }
}