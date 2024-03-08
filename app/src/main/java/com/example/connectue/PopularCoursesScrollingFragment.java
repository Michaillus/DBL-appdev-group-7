package com.example.connectue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class PopularCoursesScrollingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular_courses_scrolling, container, false);
        LinearLayout scrollViewLayout = view.findViewById(R.id.scrollViewLayout);

        // Example: Adding 5 clickable cards dynamically
        for (int i = 0; i < 5; i++) {
            View cardView = inflater.inflate(R.layout.fragment_popular_courses_scrolling, null);
            scrollViewLayout.addView(cardView);

            // Optionally, set a tag or other data to identify each card
            cardView.setTag(i);
        }
        return view;
    }
}