package com.example.connectue.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.util.TypedValue;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.connectue.R;
import com.example.connectue.activities.SearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigationrail.NavigationRailView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import com.google.android.material.tabs.TabLayout;

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
    public EditText searchEt;
    private ListView searchListView;

    private String TAG = "ChannelsFragUtil: ";

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
        View view;
        int orientation = getResources().getConfiguration().orientation;
        // Inflate layout based on orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Inflate portrait layout
            view = inflater.inflate(R.layout.fragment_channels, container, false);
            TabLayout tabLayout = view.findViewById(R.id.tablayout_channel_menu);
            setupTabLayout(tabLayout);
        } else {
            // Inflate landscape layout
            view = inflater.inflate(R.layout.fragment_channels_land, container, false);
            NavigationRailView navigationRailView = view.findViewById(R.id.navigation_rail);
            setupNavigationRail(navigationRailView);
        }
        popularText = view.findViewById(R.id.popular);
        myCoursesText = view.findViewById(R.id.mycourses);
        majorsText = view.findViewById(R.id.majors);

        searchBtn = view.findViewById(R.id.search_button);
        searchEt = view.findViewById(R.id.searchEditText);

        searchListView = view.findViewById(R.id.searchListView);

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

        showSearchListView(); // each time change the search EditText, the list view will update

        horizontalScroller = new PopularCoursesScrollingFragment();
        majorsVerticalScrollingFragment = new MajorsVerticalScrollingFragment();
        myCoursesVerticalFragment = new MyCoursesVerticalFragment();
        showCoursesView();


        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerViewPopCourses, horizontalScroller);
        transaction.replace(R.id.fragmentContainerViewMyCourses, myCoursesVerticalFragment);
        transaction.replace(R.id.fragmentContainerViewMajors, majorsVerticalScrollingFragment);
        transaction.commit();
        // Inflate the layout for this fragment
        return view;
    }


    /**
     * This helper functions sets a listener for the tab layout
     * to know which tab is selected and therefore which fragment to
     * display to the user,
     * @param tabLayout
     */
    private void setupTabLayout(TabLayout tabLayout) {
        // Setup tab layout for portrait orientation
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabText = tab.getText().toString();
                if (tabText.equals("Courses")) {
                    Log.d(TAG, "courses");
                    showCoursesView();
                } else if (tabText.equals("Majors")) {
                    Log.d(TAG, "majors");
                    showMajorsView();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                /**
                 * this method is empty because there is no behaviour when
                 * a tab is unselected.
                 */
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                /**
                 * this method is empty because there is no behaviour when
                 * a tab is reselected.
                 */
            }
        });
    }

    /**
     * This helper functions sets a listener for the navigation rail
     * to know which tab is selected and therefore which fragment to
     * display to the user,
     * @param navigationRailView
     */
    private void setupNavigationRail(NavigationRailView navigationRailView) {
        // Setup navigation rail for landscape orientation
        navigationRailView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_courses) {
                    Log.d(TAG, "courses");
                    showCoursesView();
                    return true;
                } else if (item.getItemId() == R.id.nav_majors) {
                    Log.d(TAG, "majors");
                    showMajorsView();
                    return true;
                } else {
                    Log.d(TAG, "courses");
                    showCoursesView();
                    return true;
                }
            }
        });
    }

    /**
     * this helper function displays the
     * courses fragment to users.
     */
    private void showCoursesView() {
        // Set height of search bar to 50dp
        int searchEtHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()
        );
        ViewGroup.LayoutParams searchEtLayoutParams = searchEt.getLayoutParams();
        searchEtLayoutParams.height = searchEtHeight;
        searchEt.setLayoutParams(searchEtLayoutParams);

        // Set height of search button to 40dp
        int searchBtnHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()
        );
        ViewGroup.LayoutParams searchBtnLayoutParams = searchBtn.getLayoutParams();
        searchBtnLayoutParams.height = searchBtnHeight;
        searchBtn.setLayoutParams(searchBtnLayoutParams);
        popularText.setVisibility(View.VISIBLE);
        myCoursesText.setVisibility(View.VISIBLE);
        majorsText.setVisibility(View.GONE);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.hide(majorsVerticalScrollingFragment);
        transaction.show(horizontalScroller);
        transaction.show(myCoursesVerticalFragment);
        transaction.commit();
    }

    /**
     * this helper function displays the
     * majors fragment to users
     */
    private void showMajorsView() {
        // Set height of search bar to 0
        ViewGroup.LayoutParams searchEtLayoutParams = searchEt.getLayoutParams();
        searchEtLayoutParams.height = 0;
        searchEt.setLayoutParams(searchEtLayoutParams);

        // Set height of search button to 0
        ViewGroup.LayoutParams searchBtnLayoutParams = searchBtn.getLayoutParams();
        searchBtnLayoutParams.height = 0;
        searchBtn.setLayoutParams(searchBtnLayoutParams);
        popularText.setVisibility(View.GONE);
        myCoursesText.setVisibility(View.GONE);
        majorsText.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.show(majorsVerticalScrollingFragment);
        transaction.hide(horizontalScroller);
        transaction.hide(myCoursesVerticalFragment);
        transaction.commit();
    }

    /**
     * This helper method displays a list of suggested
     * courses when a user types a query in the search bar
     */
    //Made public for test file access
    public void showSearchListView() {
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /**
                 * this method is empty because there is
                 * no behaviour prior to any text input.
                 */
            }

            /**
             * When text input changes, make a new query
             * and update the suggested courses.
             * @param s the new input
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // get the text content from search edit text
                Log.i(TAG, "text change detected" + s);
                String searchText = s.toString().trim();
                searchText = searchText.toUpperCase();

                if (searchText.isEmpty()) {
                    searchListView.setAdapter(null);
                    Log.i(TAG, "without following query");
                    return;
                }
                Log.i(TAG, "go on query");

                searchListView.setVisibility(View.VISIBLE);

                Query query = FirebaseFirestore.getInstance()
                        .collection("courses")
                        .whereGreaterThanOrEqualTo("code", searchText)
                        .whereLessThan("code", searchText + "\uf8ff");

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> courseCodes = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // get course code and add it into the list
                                String courseCode = document.getString("code");
                                courseCodes.add(courseCode);
                            }

                            // renew list view
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, courseCodes);
                            searchListView.setAdapter(adapter);

                            searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selectedItemText = (String) parent.getItemAtPosition(position);
                                    // give the item clicked to search bar
                                    searchEt.setText(selectedItemText);
                                    searchListView.setVisibility(View.GONE);
                                }

                            });

                        } else {
                            // deal with the situation that it fails to search
                            Log.e("Fail to search.", "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
            @Override
            public void afterTextChanged(Editable s) {
            }

        });
    }

}