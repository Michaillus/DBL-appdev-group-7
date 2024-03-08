package com.example.connectue;

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
    private String TAG2 = "AdapterPosts: ";

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

//        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //return inflater.inflate(R.layout.fragment_home, container, false);

        postList = new ArrayList<>();
        //loadPostsFromFirestore();
        db.collection("posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Post post = new Post(document.getString("publisher"),
                                    document.getString("text"),
                                    document.getString("photoULR"),
                                    document.getLong("likes").intValue(),
                                    document.getLong("comments").intValue());
                            postList.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                        Log.d(TAG2, "loadPostsFromFirestore: " + postList.get(postList.size()-1).pDescription);
                    } else {
                        Log.d(TAG2, "Error getting documents: ", task.getException());
                    }
                });
        //Initialize ReclerView

        Post post = new Post("I", "text", null, 0, 0);
        Post post2 = new Post("You", "text", null, 0, 0);

//        postList.add(post);
//        postList.add(post2);

        Log.d(TAG2, "onCreateView: postList size: " + postList.size());

        postAdapter = new AdapterPosts(postList);
//        RecyclerView recyclerView = view.findViewById(R.id.postsRecyclerView);
        binding.postsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.postsRecyclerView.setHasFixedSize(true);

        binding.postsRecyclerView.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();
        Log.d(TAG2, "onCreateView: adapter list size: " + binding.postsRecyclerView.getAdapter().getItemCount());


        return root;
//        return view;
    }

    private void loadPostsFromFirestore() {
        db.collection("posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Post post = new Post(document.getString("publisher"),
                                    document.getString("text"),
                                    document.getString("photoULR"),
                                    document.getLong("likes").intValue(),
                                    document.getLong("comments").intValue());
                            postList.add(post);
                        }
                        //postAdapter.notifyDataSetChanged();
                        Log.d(TAG2, "loadPostsFromFirestore: " + postList.get(postList.size()-1).pDescription);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}