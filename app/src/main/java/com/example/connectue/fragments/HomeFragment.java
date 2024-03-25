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
import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.managers.PostManager;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.Post;
import com.example.connectue.model.User2;
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
public class HomeFragment extends Fragment {

    /**
     * Class tag for logs.
     */
    private static final String tag = "HomeFragment";

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

    public HomeFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

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

    private void initRecyclerView(List<Post> postList, RecyclerView postRecyclerView) {
        postAdapter = new PostAdapter(postList, fragmentManager);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        postRecyclerView.setHasFixedSize(false);
        postRecyclerView.setAdapter(postAdapter);
    }

    private void loadPosts(List<Post> postList) {
        int postsPerChunk = 4;
        postManager.downloadRecent(postsPerChunk, new FireStoreDownloadCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> data) {
                postList.addAll(data);
                postAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(tag, "Error while downloading posts", e);
            }
        });
    }

    private void displayAddPostButton(View root) {
        UserManager userManager = new UserManager(FirebaseFirestore.getInstance(), "users");
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ImageButton addPostBtn = root.findViewById(R.id.addPostBtn);
        userManager.downloadOne(currentUid, new FireStoreDownloadCallback<User2>() {
            @Override
            public void onSuccess(User2 data) {
                if (data.isVerified()) {
                    addPostBtn.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), AddPostActivity.class);
                        startActivity(intent);
                    });
                } else {
                    addPostBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(tag, "Error while retrieving user from the database", e);
            }
        });
    }

    private void getPostsOnScroll(List<Post> postList, RecyclerView postRecyclerView) {
        postRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) binding.postsRecyclerView.getLayoutManager();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}