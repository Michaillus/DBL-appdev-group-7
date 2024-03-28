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
import com.example.connectue.activities.StudyUnitViewActivity;
import com.example.connectue.adapters.ReviewAdapter;
import com.example.connectue.databinding.FragmentReviewsBinding;
import com.example.connectue.interfaces.ConditionCheckCallback;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.interfaces.ItemExistsCallback;
import com.example.connectue.managers.ReviewManager;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.Review;
import com.example.connectue.model.StudyUnit;
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
 * A fragment that shows a feed of reviews of a study unit (course or major) and add a study unit
 * button.
 */
public class ReviewsFragment extends Fragment {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "ReviewsFragment";

    private FragmentReviewsBinding binding;

    /**
     * Adapter that is responsible for outputting review list to UI.
     */
    private ReviewAdapter reviewAdapter;

    /**
     * Manager for database requests for study unit's reviews collection.
     */
    private ReviewManager reviewManager;

    /**
     * Indicates if posts are currently loading from database.
     */
    private Boolean isLoading = false;

    /**
     * Indicates if posts are currently loading from database.
     */
    private FragmentManager fragmentManager;

    /**
     * Study unit model of the opened page.
     */
    StudyUnit studyUnit;

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

        // Initialize the study unit.
        studyUnit = retrieveStudyUnit();

        // Define reviews recycler view.
        RecyclerView rewievsRecyclerView = binding.recyclerViewReview;

        // Initialize database post manager.
        reviewManager = new ReviewManager(FirebaseFirestore.getInstance(),
                studyUnit.getReviewCollectionName(),
                studyUnit.getReviewLikeCollectionName(),
                studyUnit.getReviewDislikeCollectionName(),
                studyUnit.getReviewCommentCollectionName());

        // Initializing the list of posts in the feed.
        List<Review> reviewList = new ArrayList<>();

        //Initialize RecyclerView
        initRecyclerView(reviewList, rewievsRecyclerView);

        // Upload from database and display first chunk of posts
        loadReviews(reviewList);

        // Display the createPostBtn only for verified users
        displayAddReviewButton(root);

        // Add a scroll listener to the RecyclerView
        getPostsOnScroll(reviewList, rewievsRecyclerView);

        return root;
    }

    private void initRecyclerView(List<Review> reviewList, RecyclerView reviewsRecyclerview) {
        reviewAdapter = new ReviewAdapter(reviewList, fragmentManager, studyUnit);
        reviewsRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        reviewsRecyclerview.setHasFixedSize(false);
        reviewsRecyclerview.setAdapter(reviewAdapter);
    }

    private void loadReviews(List<Review> reviewList) {
        int reviewsPerChunk = 6;

        reviewManager.downloadRecent(studyUnit.getId(), reviewsPerChunk, new ItemDownloadCallback<List<Review>>() {
            @Override
            public void onSuccess(List<Review> data) {
                reviewList.addAll(data);
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
                        Log.e(TAG, studyUnit.studyUnitToString());
                        intent.putExtra("course", studyUnit.studyUnitToString());
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
                    reviewManager.hasUserReviewedStudyUnit(studyUnit.getId(), userId, new ItemExistsCallback() {
                        @Override
                        public void onSuccess(boolean exists) {
                            if (!exists) {
                                // User is a student and has no reviews on the study unit
                                callback.onSuccess(true);
                            } else {
                                // User has a review on the study unit
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

    private void getPostsOnScroll(List<Review> reviewList, RecyclerView reviewsRecyclerView) {
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
                    loadReviews(reviewList);
                }
            }
        });
    }

    /**
     * Retrieves the study unit model of current page.
     */
    private StudyUnit retrieveStudyUnit() {
        StudyUnitViewActivity studyUnitViewActivity = (StudyUnitViewActivity) getActivity();
        if (studyUnitViewActivity != null) {
            return studyUnitViewActivity.getStudyUnit();
        } else {
            return new StudyUnit("0", "0", StudyUnit.StudyUnitType.COURSE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}