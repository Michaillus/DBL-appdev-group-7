package com.example.connectue;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connectue.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Firestore
    private FirebaseFirestore db;
    List<String> programs;

    private FragmentHomeBinding binding;
    private List<Post> postList;
    private AdapterPosts postAdapter;

    // Number of posts loaded at once to the feed.
    final int postsPerChunk = 4;

    // Query for loading posts from the database.
    Query postsQuery;

    // Last post that was loaded from the database.
    DocumentSnapshot lastVisiblePost;

    private Boolean isLoading = false;

    private FragmentManager fragmentManager;

    private String TAG = "HomePageUtil: ";

    public HomeFragment() {
        // Default constructor
    }

    public HomeFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment HomeFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static HomeFragment newInstance(String param1, String param2) {
//        HomeFragment fragment = new HomeFragment(this.fragmentManager);
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        postList = new ArrayList<>();

        //Initialize ReclerView
        postAdapter = new AdapterPosts(postList, fragmentManager);
        binding.postsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.postsRecyclerView.setHasFixedSize(false);
        binding.postsRecyclerView.setAdapter(postAdapter);

        // Initialize database query for post retrieval and load first chunk of posts to the feed.
        postsQuery = db.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING);
        loadChunkOfPosts();

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
                        && totalItemCount >= PAGE_SIZE) { // Assuming PAGE_SIZE is the number of items to load per page
                    // Load more items

                    isLoading = true;
                    loadChunkOfPosts();
                }
            }
        });

        binding.postsRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return root;
    }

    /**
     * Load chunk of postsPerChunk posts from database and display to the feed.
     */
    private void loadChunkOfPosts() {
        Query currentQuery;
        if (lastVisiblePost == null) {
            currentQuery = postsQuery.limit(postsPerChunk);
        } else {
            currentQuery = postsQuery.startAfter(lastVisiblePost).limit(postsPerChunk);
        }
        currentQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                if (!snapshot.isEmpty()) {
                    lastVisiblePost = snapshot.getDocuments().get(task.getResult().size() - 1);
                    displayPosts(task.getResult());
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Displays posts that were loaded from the database to the post feed in UI.
     * @param snapshot object containing all the posts that were loaded from database.
     */
    private void displayPosts(QuerySnapshot snapshot) {
        for (QueryDocumentSnapshot document : snapshot) {
            Post.createPost(document, new PostCreateCallback() {
                @Override
                public void onPostCreated(Post post) {
                    postList.add(post);
                    postAdapter.notifyDataSetChanged();
                    isLoading = false;
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        lastVisiblePost = null;
    }
}