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
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomePageUtil: ";

    private FragmentHomeBinding binding;

    // Firebase instance
    private FirebaseFirestore db;

    // List of posts to output in feed
    private List<Post> postList;

    // Adapter that is responsible for outputting posts to UI
    private PostAdapter postAdapter;

    // Number of posts loaded at once to the feed
    private final int postsPerChunk = 4;

    // Manager for database requests for posts collection
    private PostManager postManager;

    // Indicates if posts are currently loading from database
    private Boolean isLoading = false;

    private FragmentManager fragmentManager;

    private UserManager userManager;
    private ImageButton createPostBtn;

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

        // Initialize database post manager.
        postManager = new PostManager(FirebaseFirestore.getInstance(),
                Post.POST_COLLECTION_NAME, Post.POST_LIKE_COLLECTION_NAME,
                Post.POST_DISLIKE_COLLECTION_NAME, Post.POST_COMMENT_COLLECTION_NAME);

        //Initializing the list of posts
        postList = new ArrayList<>();

        //Initialize RecyclerView
        postAdapter = new PostAdapter(postList, fragmentManager);
        binding.postsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.postsRecyclerView.setHasFixedSize(false);
        binding.postsRecyclerView.setAdapter(postAdapter);

        // Upload from database and display first chunk of posts
        loadPosts();

        // Display the createPostBtn only for verified users
        db = FirebaseFirestore.getInstance();
        userManager = new UserManager(db, "users");
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        createPostBtn = root.findViewById(R.id.createPostBtn);
        userManager.downloadOne(currentUid, new FireStoreDownloadCallback<User2>() {
            @Override
            public void onSuccess(User2 data) {
                if (data.isVerified()) {
                    createPostBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), AddPostActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    createPostBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        // Add a scroll listener to the RecyclerView
        binding.postsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    loadPosts();
                }
            }
        });

        return root;
    }

    public void loadPosts() {
        postManager.downloadRecent(postsPerChunk, new FireStoreDownloadCallback<List<Post>>() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        postManager.resetLastRetrieved();
    }
}