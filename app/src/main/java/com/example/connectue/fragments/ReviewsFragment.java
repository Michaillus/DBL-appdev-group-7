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
import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.managers.ReviewManager;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.Review;
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
    private static final String tag = "ReviewFragment";

    private FragmentReviewsBinding binding;

    /**
     * Adapter that is responsible for outputting posts to UI.
     */
    private ReviewAdapter reviewAdapter;

    /**
     * Manager for database requests for posts collection.
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
        reviewManager = new ReviewManager(FirebaseFirestore.getInstance(),
                Review.REVIEW_COLLECTION_NAME, Review.REVIEW_LIKE_COLLECTION_NAME,
                Review.REVIEW_DISLIKE_COLLECTION_NAME, Review.REVIEW_COMMENT_COLLECTION_NAME);

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
        reviewAdapter = new ReviewAdapter(reviewList, fragmentManager);
        reviewsRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        reviewsRecyclerview.setHasFixedSize(false);
        reviewsRecyclerview.setAdapter(reviewAdapter);
    }

    private void loadReviews(List<Review> reviewList) {
        int reviewsPerChunk = 4;


        String courseId = retrieveCourseId();

        reviewManager.downloadRecent(courseId, reviewsPerChunk, new FireStoreDownloadCallback<List<Review>>() {
            @Override
            public void onSuccess(List<Review> data) {
                reviewList.addAll(data);
                reviewAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(tag, "Error while downloading reviews", e);
            }
        });
    }

    private String retrieveCourseId() {
        CourseViewActivity courseViewActivity = (CourseViewActivity) getActivity();
        if (courseViewActivity != null) {
            return courseViewActivity.getCourse();
        } else {
            return "";
        }
    }

    private void displayAddReviewButton(View root) {
        UserManager userManager = new UserManager(FirebaseFirestore.getInstance(), "users");
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ExtendedFloatingActionButton addReviewBtn = root.findViewById(R.id.addReviewBtn);
        userManager.downloadOne(currentUid, new FireStoreDownloadCallback<User2>() {
            @Override
            public void onSuccess(User2 data) {
                if (data.isVerified()) {
                    addReviewBtn.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), AddReviewActivity.class);
                        startActivity(intent);
                    });
                } else {
                    addReviewBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(tag, "Error while retrieving user from the database", e);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}