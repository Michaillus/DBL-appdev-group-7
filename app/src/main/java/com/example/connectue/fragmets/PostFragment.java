package com.example.connectue.fragmets;

import android.content.Intent;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectue.R;
import com.example.connectue.activities.AddPostActivity;
import com.example.connectue.activities.MainActivity;
import com.example.connectue.interfaces.FireStoreLikeCallback;
import com.example.connectue.interfaces.UserNameCallback;
import com.example.connectue.adapters.CommentAdapter;
import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.managers.PostManager;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.Comment;
import com.example.connectue.model.Post;
import com.example.connectue.model.User2;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private ImageButton backBtn;

    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;


    private FirebaseFirestore db;
    DocumentReference postRef;
    private Post currentPost;

    private String postId;

    private CollectionReference commentsRef;
    private String userId;

    private PostManager postManager;
    private UserManager userManager;

    private FragmentManager fragmentManager;

    /**
     * Class tag for logs.
     */
    private static final String TAG = "PostFragment class: ";

    /**
     * Number of posts that are retrieved and displayed each time user reaches the bottom
     * of the screen.
     */
    int commentsPerChunk = 4;

    public PostFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment PostFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static PostFragment newInstance(String param1, String param2) {
//
//        PostFragment fragment = new PostFragment(new FragmentManager() {
//        });
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postManager = new PostManager(FirebaseFirestore.getInstance(),
                Post.POST_COLLECTION_NAME, Post.POST_LIKE_COLLECTION_NAME,
                Post.POST_DISLIKE_COLLECTION_NAME, Post.POST_COMMENT_COLLECTION_NAME);
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
        commentAdapter = new CommentAdapter(getContext(), commentList);
        db = FirebaseFirestore.getInstance();
        commentsRef = FirebaseFirestore.getInstance().collection("post-comments");

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
        backBtn = view.findViewById(R.id.backFromPostBtn);

    }

    private void initCommentRecyclerView(View view) {
        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentsRecyclerView.setAdapter(commentAdapter);
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
        loadComments(postId);

        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        postManager.isLiked(postId, currentUid, new FireStoreLikeCallback() {
            @Override
            public void onSuccess(Boolean isLiked) {
                if (!isLiked) {
                    likePostBtn.setImageResource(R.drawable.like_icon);
                } else {
                    likePostBtn.setImageResource(R.drawable.liked_icon);
                }
            }

            @Override
            public void onFailure(Exception e) {}
        });

        // When the user click the like button
        likePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postManager.likeOrUnlike(currentPost, currentUid, new FireStoreLikeCallback() {
                    @Override
                    public void onSuccess(Boolean isLiked) {
                        if (!isLiked) {
                            likePostBtn.setImageResource(R.drawable.like_icon);
                        } else {
                            likePostBtn.setImageResource(R.drawable.liked_icon);
                        }
                        numOfLikes.setText(String.valueOf(currentPost.getLikeNumber()));
                    }

                    @Override
                    public void onFailure(Exception e) {}
                });
            }
        });

        // Back to homeFragment
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new HomeFragment(fragmentManager));
                fragmentTransaction.commit();
            }
        });
    }

    public void loadComments(String postId) {
        Log.e("test", "test");
        postManager.downloadRecentComments(postId, commentsPerChunk,
                new FireStoreDownloadCallback<List<Comment>>() {

            @Override
            public void onSuccess(List<Comment> comments) {
                Log.e("test", String.valueOf(comments.size()));
                commentList.addAll(comments);
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error while downloading comments", e);
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
        Comment comment = new Comment(userId, commentText, postId);
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
                                //comment.setPublisherName(user.getFullName());
                                commentList.add(0, comment);
                                // Notify the RecyclerView adapter about the dataset change
                                commentAdapter.notifyItemInserted(0);

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
