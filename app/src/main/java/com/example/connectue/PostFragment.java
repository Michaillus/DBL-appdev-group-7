package com.example.connectue;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for the post details page.
 * When users click a post on home page, they will be directed to this page.
 */
public class PostFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView publisherName;
    private TextView publisherTime;
    private ImageView postImage;
    private TextView postDescription;
    private ImageButton likePostBtn;
    private TextView numOfLikes;
    private EditText addComment;
    private ImageButton sendCommentBtn;

    private RecyclerView commentsRecyclerView;
    private AdapterComments adapterComments;
    private List<Comment> commentList;


    private FirebaseFirestore db;
    DocumentReference postRef;

    private String TAG = "Test";

    public PostFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // Setup views
        initView(view);

        // Get current postId
        Bundle bundle = getArguments();
        assert bundle != null;
        String postId = bundle.getString("postId");

        commentList = new ArrayList<>();
        adapterComments = new AdapterComments(getContext(), commentList);
        db = FirebaseFirestore.getInstance();

        // Initialize comments RecyclerView
        initCommentRecyclerView(view);

        // Load contents from Firestore
        loadContentsFromFirestore(postId);


        return view;
    }

    private void initView(View view) {
        publisherName = view.findViewById(R.id.publisherNameTextView);
        publisherTime = view.findViewById(R.id.publishTimePostTextView);
        postImage = view.findViewById(R.id.postImageInPostImageView);
        postDescription = view.findViewById(R.id.postDescriptionTextView);
        likePostBtn = view.findViewById(R.id.likeAPostBtn);
        numOfLikes = view.findViewById(R.id.numOfLikesPostTv);
        addComment = view.findViewById(R.id.addPostCommentET);
        sendCommentBtn = view.findViewById(R.id.sendPostCommentBtn);

    }

    private void initCommentRecyclerView(View view) {
        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentsRecyclerView.setAdapter(adapterComments);
    }

    private void loadContentsFromFirestore(String postId) {
        // Load current post
        postRef = db.collection("posts").document(postId);
        postRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Document exists, you can retrieve its data
                Post.createPost(documentSnapshot, new PostCreateCallback() {
                    @Override
                    public void onPostCreated(Post post) {
                        publisherName.setText(post.getPublisherName());
                        Post.loadImage(postImage, post.getImageUrl());
                        postDescription.setText(post.getDescription());
                        numOfLikes.setText(String.valueOf(post.getLikeNumber()));
                    }
                });
                // Load comments of this post
                loadCommentsFromFirestore(postId);
            } else {
                // Document doesn't exist
                Log.d(TAG, "No such document");
            }
        }).addOnFailureListener(e -> {
            // Error handling
            Log.e(TAG, "Error getting document", e);
        });
    }

    private void loadCommentsFromFirestore(String postId) {
        // Query for retrieving comments for the current post
        // Newest comments are shown first
        Query commentQuery = db.collection("comments")
                .whereEqualTo("parentId", postId)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        commentQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Convert each comment document to a Comment object
                    Comment comment = document.toObject(Comment.class);
                    User.fetchUserName(document.getString("userId"), new UserNameCallback() {
                        @Override
                        public void onUserNameFetched(String userName) {
                            comment.setPublisherName(userName);
                            commentList.add(comment);
                            adapterComments.notifyDataSetChanged();
                        }
                    });

                }

            } else {
                Log.d(TAG, "Error getting comments: ", task.getException());
            }
        });
    }

}
