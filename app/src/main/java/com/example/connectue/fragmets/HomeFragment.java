package com.example.connectue.fragmets;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connectue.activities.AddPostActivity;
import com.example.connectue.adapters.PostAdapter;
import com.example.connectue.databinding.FragmentHomeBinding;
import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.interfaces.FireStoreUploadCallback;
import com.example.connectue.managers.CommentManager;
import com.example.connectue.managers.PostManager;
import com.example.connectue.model.Comment;
import com.example.connectue.model.Post;
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

        binding.createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                startActivity(intent);
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
    }
}