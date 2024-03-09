package com.example.connectue;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChannelsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChannelsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private BottomNavigationView navigationView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView popularText;
    private TextView myCoursesText;
    private TextView majorsText;
    private PopularCoursesScrollingFragment horizontalScroller;
    private MajorsVerticalScrollingFragment majorsVerticalScrollingFragment;
    private MyCoursesVerticalFragment myCoursesVerticalFragment;

    private Button searchBtn;
    private EditText searchEt;

    public ChannelsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChannelsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChannelsFragment newInstance(String param1, String param2) {
        ChannelsFragment fragment = new ChannelsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channels, container, false);

        popularText = view.findViewById(R.id.popular);
        myCoursesText = view.findViewById(R.id.mycourses);
        majorsText = view.findViewById(R.id.majors);
        navigationView = view.findViewById(R.id.coursemenu);

        searchBtn = view.findViewById(R.id.search_button);
        searchEt = view.findViewById(R.id.searchEditText);

        // Button to trigger to search activity
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String searchText = searchEt.getText().toString();
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("searchText", searchText);
                getActivity().startActivity(intent);
            }
        });


        horizontalScroller = new PopularCoursesScrollingFragment();
        majorsVerticalScrollingFragment = new MajorsVerticalScrollingFragment();
        myCoursesVerticalFragment = new MyCoursesVerticalFragment();
        showCoursesView();
        navigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle item selection
                int itemId = item.getItemId();
                if (itemId == R.id.coursesClick) {
                    showCoursesView();
                } else if (itemId == R.id.majorsClick) {
                    showMajorsView();
                }

                return true;
            }
        });

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerViewPopCourses, horizontalScroller);
        transaction.replace(R.id.fragmentContainerViewMyCourses, myCoursesVerticalFragment);
        transaction.replace(R.id.fragmentContainerViewMajors, majorsVerticalScrollingFragment);
        transaction.commit();
        // Inflate the layout for this fragment
        return view;
    }

    private void showCoursesView() {
        searchEt.setVisibility(View.VISIBLE);
        searchBtn.setVisibility(View.VISIBLE);
        popularText.setVisibility(View.VISIBLE);
        myCoursesText.setVisibility(View.VISIBLE);
        majorsText.setVisibility(View.GONE);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.hide(majorsVerticalScrollingFragment);
        transaction.show(horizontalScroller);
        transaction.show(myCoursesVerticalFragment);
        transaction.commit();
    }
    private void showMajorsView() {
        searchEt.setVisibility(View.INVISIBLE);
        searchBtn.setVisibility(View.INVISIBLE);
        popularText.setVisibility(View.GONE);
        myCoursesText.setVisibility(View.GONE);
        majorsText.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.show(majorsVerticalScrollingFragment);
        transaction.hide(horizontalScroller);
        transaction.hide(myCoursesVerticalFragment);
        transaction.commit();
    }
}