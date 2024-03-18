package com.example.connectue.fragmets;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connectue.R;
import com.example.connectue.adapters.PostAdapter;
import com.example.connectue.adapters.ReviewAdapter;
import com.example.connectue.firestoreManager.PostManager;
import com.example.connectue.firestoreManager.ReviewManager;
import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.interfaces.FireStoreUploadCallback;
import com.example.connectue.model.Post;
import com.example.connectue.model.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewsFragment extends Fragment {

    private static final String TAG = "ReviewFragment class: ";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // List of posts to output in feed.
    private List<Review> reviewList;

    // Adapter that is responsible for outputting posts to UI.
    private ReviewAdapter reviewAdapter;

    // Number of posts loaded at once to the feed.
    private final int postsPerChunk = 4;

    // Manager for database requests for posts collection.
    private ReviewManager reviewManager;

    // Indicates if posts are currently loading from database
    private Boolean isLoading = false;

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

        RecyclerView reviewRecyclerView = view.findViewById(R.id.recyclerView_review);

        // Initialize database post manager.
        reviewManager = new ReviewManager(FirebaseFirestore.getInstance(), "reviews",
                "review-likes", "review-dislikes");

        // Uploading posts for test.
        // TODO delete
        Review newReview = new Review(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                "This course sucks", 2L);
        reviewManager.upload(newReview, new FireStoreUploadCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "post uploaded successfully");
            }
        });
        // TODO end


        // Initializing list of reviews
        reviewList = new ArrayList<>();

        reviewAdapter = new ReviewAdapter(getContext(), reviewList);
        reviewRecyclerView.setAdapter(reviewAdapter);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Upload from database and display first chunk of posts
        loadPosts();

        // Add a scroll listener to the RecyclerView
        reviewRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) reviewRecyclerView.getLayoutManager();
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
                    loadPosts();
                }
            }
        });

        // Inflate the layout for this fragment
        return view;

    }

    public void loadPosts() {
        reviewManager.downloadRecent(postsPerChunk, new FireStoreDownloadCallback<List<Review>>() {
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
}