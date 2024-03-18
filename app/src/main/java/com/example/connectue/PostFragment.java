package com.example.connectue;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectue.firestoreManager.FireStoreDownloadCallback;
import com.example.connectue.firestoreManager.PostManager;
import com.example.connectue.firestoreManager.UserManager;
import com.example.connectue.model.User2;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private Post currentPost;
    private Boolean isPostLiked;

    private String postId;

    private CollectionReference commentsRef;
    private String userId;

    private PostManager postManager;
    private UserManager userManager;

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
        postManager = new PostManager(FirebaseFirestore.getInstance(),
                "posts", "posts-likes", "posts-dislikes");
        userManager = new UserManager(FirebaseFirestore.getInstance(), "users");
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
        postId = bundle.getString("postId");

        commentList = new ArrayList<>();
        adapterComments = new AdapterComments(getContext(), commentList);
        db = FirebaseFirestore.getInstance();
        commentsRef = FirebaseFirestore.getInstance().collection("comments");

        // Initialize comments RecyclerView
        initCommentRecyclerView(view);

        // Load contents from Firestore
        loadContentsFromFirestore();

        // Upload comment function
        initCreateComment();

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

    private void loadContentsFromFirestore() {
        postManager.downloadOne(postId, new FireStoreDownloadCallback<Post>() {
            @Override
            public void onSuccess(Post post) {
                Post.loadImage(postImage, post.getImageUrl());
                postDescription.setText(post.getText());
                numOfLikes.setText(String.valueOf(post.getLikeNumber()));
                currentPost = post;

                userManager.downloadOne(post.getPublisherId(), new FireStoreDownloadCallback<User2>() {
                    @Override
                    public void onSuccess(User2 publisher) {
                        publisherName.setText(publisher.getFullName());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Error getting user", e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error getting post", e);
            }
        });
        // Load comments of this post
        loadCommentsFromFirestore(postId);

        likePostBtn
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isPostLiked) {
                        //Todo: like a post
                        }
                    }
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

    private void initCreateComment() {
        sendCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = addComment.getText().toString();
                // Check if the comment is not empty
                if (!TextUtils.isEmpty(commentText)) {
                    // Call a method to upload the comment to Firestore
                    uploadCommentToFirestore(commentText);
                    // Clear the EditText after submitting the comment
                    addComment.setText("");
                } else {
                    // Show an error message or toast if the comment is empty
                    Toast.makeText(getContext(), "Please enter a comment", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void uploadCommentToFirestore(String commentText) {
        postRef = db.collection("posts").document(postId);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Date date = new Date();
        Comment comment = new Comment(userId, commentText, postId, date);
        Timestamp timestamp = new Timestamp(date);
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("content", commentText);
        commentData.put("parentId", postId);
        commentData.put("timestamp", timestamp);
        commentData.put("userId", userId);
        commentsRef.add(commentData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Update the UI to reflect the new comment
                        userManager.downloadOne(userId, new FireStoreDownloadCallback<User2>() {
                            @Override
                            public void onSuccess(User2 user) {
                                comment.setPublisherName(user.getFullName());
                                commentList.add(0, comment);
                                // Notify the RecyclerView adapter about the dataset change
                                adapterComments.notifyItemInserted(0);

                                // Update comment number of current post.
                                currentPost.incrementCommentNumber();
                                postRef.update("comments", currentPost.getCommentNumber());
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e(TAG, "Error getting user document", e);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding comment", e);
                        // Handle the error
                    }
                });
    }

}
