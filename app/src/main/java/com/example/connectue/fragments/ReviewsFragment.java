package com.example.connectue.fragments;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connectue.R;
import com.example.connectue.activities.AddReviewActivity;
import com.example.connectue.activities.CourseViewActivity;
import com.example.connectue.adapters.ReviewAdapter;
import com.example.connectue.databinding.FragmentReviewsBinding;
import com.example.connectue.interfaces.ConditionCheckCallback;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.interfaces.ItemExistsCallback;
import com.example.connectue.managers.CourseReviewManager;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.CourseReview;
import com.example.connectue.model.User2;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment for the home page with the post feed and add a post button.
 */
public class ReviewsFragment extends Fragment {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "ReviewFragment";

    private FragmentReviewsBinding binding;

    /**
     * Adapter that is responsible for outputting posts to UI.
     */
    private ReviewAdapter reviewAdapter;

    /**
     * Manager for database requests for posts collection.
     */
    private CourseReviewManager courseReviewManager;

    /**
     * Indicates if posts are currently loading from database.
     */
    private Boolean isLoading = false;

    /**
     * Indicates if posts are currently loading from database.
     */
    private FragmentManager fragmentManager;

    /**
     * Database id of the course of the opened page.
     */
    String courseId;

    public ReviewsFragment() {
        // Default constructor
    }

    public ReviewsFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentReviewsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Define reviews recycler view.
        RecyclerView rewievsRecyclerView = binding.recyclerViewReview;

        // Initialize database post manager.
        courseReviewManager = new CourseReviewManager(FirebaseFirestore.getInstance(),
                CourseReview.COURSE_REVIEW_COLLECTION_NAME, CourseReview.COURSE_REVIEW_LIKE_COLLECTION_NAME,
                CourseReview.COURSE_REVIEW_DISLIKE_COLLECTION_NAME, CourseReview.COURSE_REVIEW_COMMENT_COLLECTION_NAME);

        // Initialize course id.
        courseId = retrieveCourseId();

        // Initializing the list of posts in the feed.
        List<CourseReview> courseReviewList = new ArrayList<>();

        //Initialize RecyclerView
        initRecyclerView(courseReviewList, rewievsRecyclerView);

        // Upload from database and display first chunk of posts
        loadReviews(courseReviewList);

        // Display the createPostBtn only for verified users
        displayAddReviewButton(root);

        // Add a scroll listener to the RecyclerView
        getPostsOnScroll(courseReviewList, rewievsRecyclerView);

        return root;
    }

    private void initRecyclerView(List<CourseReview> courseReviewList, RecyclerView reviewsRecyclerview) {
        reviewAdapter = new ReviewAdapter(courseReviewList, fragmentManager);
        reviewsRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        reviewsRecyclerview.setHasFixedSize(false);
        reviewsRecyclerview.setAdapter(reviewAdapter);
    }

    private void loadReviews(List<CourseReview> courseReviewList) {
        int reviewsPerChunk = 6;

        courseReviewManager.downloadRecent(courseId, reviewsPerChunk, new ItemDownloadCallback<List<CourseReview>>() {
            @Override
            public void onSuccess(List<CourseReview> data) {
                courseReviewList.addAll(data);
                reviewAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while downloading reviews", e);
            }
        });
    }

    private void displayAddReviewButton(View root) {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ExtendedFloatingActionButton addReviewBtn = root.findViewById(R.id.addReviewBtn);
        checkUserAllowedAddReview(currentUid, new ConditionCheckCallback() {
            @Override
            public void onSuccess(boolean conditionSatisfied) {
                if (conditionSatisfied) {
                    Log.i(TAG, "User is allowed to add a review");
                    addReviewBtn.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), AddReviewActivity.class);
                        intent.putExtra("courseId", courseId);
                        startActivity(intent);
                    });
                    addReviewBtn.setVisibility(View.VISIBLE);
                } else {
                    Log.i(TAG, "User is not allowed to add a review");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while checking if user allowed to add a review", e);
            }
        });
    }

    public void checkUserAllowedAddReview(String userId, ConditionCheckCallback callback) {
        UserManager userManager = new UserManager(FirebaseFirestore.getInstance(), "users");
        userManager.downloadOne(userId, new ItemDownloadCallback<User2>() {
            @Override
            public void onSuccess(User2 data) {
                if (data.isVerified()) {
                    // User is a student
                    courseReviewManager.hasUserReviewedCourse(courseId, userId, new ItemExistsCallback() {
                        @Override
                        public void onSuccess(boolean exists) {
                            if (!exists) {
                                // User is a student and has no reviews on the course
                                callback.onSuccess(true);
                            } else {
                                // User has a review on the course
                                callback.onSuccess(false);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            callback.onFailure(e);
                        }

                    });
                } else {
                    // User is not a student
                    callback.onSuccess(false);
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }

        });
    }

    private void getPostsOnScroll(List<CourseReview> courseReviewList, RecyclerView reviewsRecyclerView) {
        reviewsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) reviewsRecyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // Check if end of the list is reached
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    // Assuming PAGE_SIZE is the number of items to load per page
                    // Load more items
                    isLoading = true;
                    loadReviews(courseReviewList);
                }
            }
        });
    }

    /**
     * Retrieves the id of the course of current page.
     */
    private String retrieveCourseId() {
        CourseViewActivity courseViewActivity = (CourseViewActivity) getActivity();
        if (courseViewActivity != null) {
            return courseViewActivity.getCourse();
        } else {
            return "";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}