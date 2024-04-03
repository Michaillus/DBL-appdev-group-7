package com.example.connectue.fragments;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.connectue.model.User;
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

    /**
     * Activity result launcher for launching the reload method on finish add review activity.
     */
    ActivityResultLauncher<Intent> activityResultLauncher;

    /**
     * Default constructor for this class.
     */
    public ReviewsFragment() {
        // Default constructor
    }

    public ReviewsFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /**
     * Called to have the fragment instantiate its UI view of review page.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the view for the fragment's UI of the review page of s specific course, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentReviewsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Defines listener for reloading the study unit view activity when user returns from
        // add review activity page.
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.e(TAG, "Does this work?");
                    StudyUnitViewActivity studyUnitViewActivity = (StudyUnitViewActivity) getActivity();
                    if (studyUnitViewActivity != null) {
                        studyUnitViewActivity.reload();
                    } else {
                        Log.e(TAG, "Unable to get Study unit view activity");
                    }

                });

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

    /**
     * Initializes the recyclerView with the provided list of reviews and sets up the adapter for views.
     * @param reviewList The list of reviews to be displayed in the recyclerView.
     * @param reviewsRecyclerview The recyclerView instance to be initialized.
     */
    private void initRecyclerView(List<Review> reviewList, RecyclerView reviewsRecyclerview) {
        // Create a new instance of ReviewAdapter and set it as the adapter for the RecyclerView
        reviewAdapter = new ReviewAdapter(reviewList, fragmentManager, studyUnit);

        // Set layout manager for RecyclerView
        reviewsRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Set whether the size of the RecyclerView will be affected by the adapter contents
        reviewsRecyclerview.setHasFixedSize(false);

        // Set the adapter for the RecyclerView
        reviewsRecyclerview.setAdapter(reviewAdapter);
    }

    /**
     * Loads reviews from the database and adds them to the provided review list.
     * This method is called to fetch a chunk of recent reviews and update the RecyclerView.
     * @param reviewList The list of reviews to which the fetched reviews will be added.
     */
    private void loadReviews(List<Review> reviewList) {
        // Define the number of reviews to be fetched per chunk
        int reviewsPerChunk = 6;

        // Download recent reviews from the database
        reviewManager.downloadRecent(studyUnit.getId(), reviewsPerChunk, new ItemDownloadCallback<List<Review>>() {
            @Override
            public void onSuccess(List<Review> data) {
                reviewList.addAll(data);

                // Notify the adapter that the data set has changed
                reviewAdapter.notifyDataSetChanged();

                // Set isLoading to false indicating that the data loading process is complet
                isLoading = false;
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while downloading reviews", e);
            }
        });
    }

    /**
     * Displays the "Add Review" button based on whether the current user is allowed to add a review or not.
     * This method checks if the user is allowed to add a review and sets the visibility of the button accordingly.
     * @param root
     */
    private void displayAddReviewButton(View root) {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ExtendedFloatingActionButton addReviewBtn = root.findViewById(R.id.addReviewBtn);

        // Check if the current user is allowed to add a review
        checkUserAllowedAddReview(currentUid, new ConditionCheckCallback() {
            @Override
            public void onSuccess(boolean conditionSatisfied) {
                if (conditionSatisfied) {
                    // If user is allowed to add a review, set click listener to navigate to AddReviewActivity.
                    Log.i(TAG, "User is allowed to add a review");
                    addReviewBtn.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), AddReviewActivity.class);
                        intent.putExtra("course", studyUnit.studyUnitToString());

                        activityResultLauncher.launch(intent);
                    });

                    // Make the "Add Review" button visible
                    addReviewBtn.setVisibility(View.VISIBLE);
                } else {
                    // If user is not allowed to add a review, hide the "Add Review" button
                    Log.i(TAG, "User is not allowed to add a review");
                    addReviewBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while checking if user allowed to add a review", e);
            }
        });
    }

    /**
     * Checks if the specified user is allowed to add a review based on their role and whether they have
     * already reviewed the study unit.
     * @param userId The ID of the user to check.
     * @param callback Callback to handle the result of the condition check.
     */
    public void checkUserAllowedAddReview(String userId, ConditionCheckCallback callback) {
        UserManager userManager = new UserManager(FirebaseFirestore.getInstance(), "users");

        // Download user information from database
        userManager.downloadOne(userId, new ItemDownloadCallback<User>() {
            @Override
            public void onSuccess(User user) {
                // Check if the user is a student or an admin
                if (user.getRole() == User.STUDENT_USER_ROLE || user.getRole() == User.ADMIT_USER_ROLE) {
                    // User is a student or admin, check if they have already reviewed the study unit
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
                    // User is not a student or an admin, they are not allowed to add a review
                    callback.onSuccess(false);
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Pass exceptions to the callback
                callback.onFailure(e);
            }

        });
    }

    /**
     * Sets up a scroll listener for the RecyclerView to detect when the user has scrolled to
     * the end of the list and triggers loading more reviews.
     * @param reviewList The list of reviews being displayed in the recyclerView.
     * @param reviewsRecyclerView The RecyclerView containing the reviews.
     */

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

    /**
     * Called when the fragment's view is being destroyed. It releases any resources associated with the view.
     * This method should be overridden to clean up references to the view and prevent memory leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}