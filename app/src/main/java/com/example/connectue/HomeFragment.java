package com.example.connectue;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connectue.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private String TAG = "HomePageUtil: ";
    private String TAG2 = "Test: ";

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

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//
//        db = FirebaseFirestore.getInstance();
//
//        programs = new ArrayList<>();
//
//        db.collection("programs")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                programs.add(document.getId());
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//
//                        Log.d(TAG, programs.toString());
//                    }
//                });
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        postList = new ArrayList<>();
        loadPostsFromFirestore();
        Log.d(TAG2, "onCreateView: first log");

        //Initialize ReclerView
        Log.d(TAG2, "Third log onCreateView: postList size: " + postList.size());

        postAdapter = new AdapterPosts(postList);
        binding.postsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.postsRecyclerView.setHasFixedSize(true);
        binding.postsRecyclerView.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();
        Log.d(TAG2, "Fourth log onCreateView: adapter list size: " + binding.postsRecyclerView.getAdapter().getItemCount());

        binding.createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    // Display posts in fireStore
    private void loadPostsFromFirestore() {
        db.collection("posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userId = document.getString("publisher");
                            if (userId != null) {
                                fetchUserName(document, new UserNameCallback() {
                                    @Override
                                    public void onUserNameFetched(String userName) {
                                        Post post = new Post(userName,
                                                document.getString("text"),
                                                document.getString("photoULR"),
                                                document.getLong("likes").intValue(),
                                                document.getLong("comments").intValue());
                                        postList.add(post);
                                        postAdapter.notifyDataSetChanged();
                                    }
                                });
                            } else {
                                // Handle the case where userId is null
                                Post post = new Post("User",
                                        document.getString("text"),
                                        document.getString("photoULR"),
                                        document.getLong("likes").intValue(),
                                        document.getLong("comments").intValue());
                                postList.add(post);
                                postAdapter.notifyDataSetChanged();
                            }
                        }
//                        Log.d(TAG2, "loadPostsFromFirestore: " + postList.get(postList.size() - 1).pDescription);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
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