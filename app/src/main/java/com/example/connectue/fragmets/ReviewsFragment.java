package com.example.connectue.fragmets;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connectue.R;
import com.example.connectue.adapters.ReviewAdapter;
import com.example.connectue.model.Review;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    

    ArrayList<Review> reviewModels = new ArrayList<>();
    //private Context ReviewsFragment;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReviewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewsFragment newInstance(String param1, String param2) {
        ReviewsFragment fragment = new ReviewsFragment();
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
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        setUpCourseReviewFragmentModels();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_review);

        ReviewAdapter adapter = new ReviewAdapter(getContext(), reviewModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return view;

    }

    private void setUpCourseReviewFragmentModels() {
        String[] uName = getResources().getStringArray(R.array.reviewerName);
        String[] date = getResources().getStringArray(R.array.date);
        String[] uText = getResources().getStringArray(R.array.reviews);
        String[] likeNum = getResources().getStringArray(R.array.likeNum);
        String[] dislikeNum = getResources().getStringArray(R.array.dislikeNum);

        for (int i = 0; i < uName.length; i++) {
            reviewModels.add(new Review(uName[i],
                    date[i],
                    uText[i],
                    R.drawable.like_icon,
                    R.drawable.dislike,
                    R.drawable.star,
                    likeNum[i],
                    dislikeNum[i]));
        }
    }
}