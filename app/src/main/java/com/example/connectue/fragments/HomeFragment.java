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
import android.widget.ImageButton;

import com.example.connectue.R;
import com.example.connectue.activities.AddPostActivity;
import com.example.connectue.adapters.PostAdapter;
import com.example.connectue.databinding.FragmentHomeBinding;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.managers.PostManager;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.Post;
import com.example.connectue.model.User;
import com.example.connectue.utils.General;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment for the home page with the post feed and add a post button.
 * Posts are displayed in a recyclerView. Post items are in PostAdapter class.
 */
public class HomeFragment extends Fragment {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "HomeFragment";

    /**
     * Binding object to bind with the home fragment layout.
     */
    private FragmentHomeBinding binding;

    /**
     * Adapter that is responsible for outputting posts to UI.
     */
    private PostAdapter postAdapter;

    /**
     * Manager for database requests for posts collection.
     */
    private PostManager postManager;

    /**
     * Indicates if posts are currently loading from database.
     */
    private Boolean isLoading = false;

    /**
     * Indicates if posts are currently loading from database.
     */
    private FragmentManager fragmentManager;

    public HomeFragment() {
        // Default constructor
    }

    /**
     * Constructor of HomeFragment. A FragmentManager object is loaded for
     * later fragment transaction process.
     * @param fragmentManager the fragmentManager of Main activity.
     */
    public HomeFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /**
     * Initialize the UIs of this fragment.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return root which is the view of this fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Define reviews recycler view.
        RecyclerView postRecyclerView = binding.postsRecyclerView;

        // Initialize database post manager.
        postManager = new PostManager(FirebaseFirestore.getInstance(),
                Post.POST_COLLECTION_NAME, Post.POST_LIKE_COLLECTION_NAME,
                Post.POST_DISLIKE_COLLECTION_NAME, Post.POST_COMMENT_COLLECTION_NAME);

        // Initializing the list of posts in the feed.
        List<Post> postList = new ArrayList<>();

        //Initialize RecyclerView
        initRecyclerView(postList, postRecyclerView);

        // Upload from database and display first chunk of posts
        loadPosts(postList);

        // Display the createPostBtn only for verified users
        displayAddPostButton(root);

        // Add a scroll listener to the RecyclerView
        getPostsOnScroll(postList, postRecyclerView);

        return root;
    }

    /**
     * Initialize the recyclerView to dynamically display posts.
     * @param postList the list of posts.
     * @param postRecyclerView the recyclerView that contains the list of posts.
     */
    private void initRecyclerView(List<Post> postList, RecyclerView postRecyclerView) {
        postAdapter = new PostAdapter(postList, fragmentManager);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        postRecyclerView.setHasFixedSize(false);
        postRecyclerView.setAdapter(postAdapter);
    }

    /**
     * Display posts
     * @param postList the list of posts to be displayed
     */
    private void loadPosts(List<Post> postList) {
        int postsPerChunk = 4;
        postManager.downloadRecent(postsPerChunk, new ItemDownloadCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> data) {
                postList.addAll(data);
                postAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while downloading posts", e);
            }
        });
    }

    /**
     * Initialize the add post button.
     * Once the user clicks, the app will be redirected to AddPostActivity.
     * @param root The view object of current fragment.
     */
    private void displayAddPostButton(View root) {
        UserManager userManager = new UserManager(FirebaseFirestore.getInstance(), "users");
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ImageButton addPostBtn = root.findViewById(R.id.addPostBtn);
        userManager.downloadOne(currentUid, new ItemDownloadCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (user.getRole() == User.STUDENT_USER_ROLE || user.getRole() == User.ADMIT_USER_ROLE) {
                    addPostBtn.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), AddPostActivity.class);
                        startActivity(intent);
                    });
                    addPostBtn.setVisibility(View.VISIBLE);
                } else {
                    addPostBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while retrieving user from the database", e);
            }
        });
    }

    /**
     * Scroll down to the end of the page to load more posts.
     * @param postList the list to hold posts
     * @param postRecyclerView the recyclerView to display posts
     */
    private void getPostsOnScroll(List<Post> postList, RecyclerView postRecyclerView) {
        postRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) postRecyclerView.getLayoutManager();
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
                    loadPosts(postList);
                }
            }
        });
    }

    /**
     * Destroy this fragment after switching to another page. Remove the binding.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}