package com.example.connectue;

import android.content.Intent;
import android.os.Bundle;

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

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
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

    private String TAG = "HomePageUtil: ";

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        postList = new ArrayList<>();

        // Initialize database query for post retrieval and load first chunk of posts to the feed.
        postsQuery = db.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING);
        loadChunkOfPosts();

        //Initialize ReclerView
        postAdapter = new AdapterPosts(postList);
        binding.postsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.postsRecyclerView.setHasFixedSize(false);
        binding.postsRecyclerView.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();

        binding.createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                startActivity(intent);
            }
        });

        binding.loadMorePostsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadChunkOfPosts();
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
            String userId = document.getString("publisher");
            String text = document.getString("text");
            String imageURL = document.getString("photoURL");

            if (userId == null) {
                Log.e(TAG, "Post publisher should not be null");
            } else if (text == null) {
                Log.e(TAG, "Post text should not be null");
            } else if (document.getLong("likes") == null) {
                Log.e(TAG, "Number of post likes should not be null");
            } else if (document.getLong("comments") == null) {
                Log.e(TAG, "Number of post comments should not be null");
            } else {
                fetchUserName(document, new UserNameCallback() {
                    @Override
                    public void onUserNameFetched(String userName) {
                        Post post = new Post(userName,
                                text,
                                imageURL,
                                document.getLong("likes").intValue(),
                                document.getLong("comments").intValue());
                        postList.add(post);
                        postAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    /**
     * Get userName of a post from firestore.
     * @param document ref to a post
     * @param callback not important
     */
    private void fetchUserName(QueryDocumentSnapshot document, UserNameCallback callback) {
        String uid = document.getString("publisher");
        if (uid != null) {
            DocumentReference userRef = db.collection("users").document(uid);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Retrieve the user name from the document snapshot
                    String userName = documentSnapshot.getString("firstName") + " " + documentSnapshot.getString("lastName");
                    Log.d("User Name", "User name: " + userName);
                    // Invoke the callback with the retrieved username
                    callback.onUserNameFetched(userName);
                } else {
                    Log.d("User Name", "User document does not exist.");
                }
            }).addOnFailureListener(e -> {
                Log.e("User Name", "Error retrieving user document: " + e.getMessage());
                // If there's an error, invoke the callback with null username
                callback.onUserNameFetched(null);
            });
        } else {
            // If uid is null, invoke the callback with null username
            callback.onUserNameFetched(null);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}