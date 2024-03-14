package com.example.connectue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectue.databinding.RowPostsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class for using recyclerView to display posts.
 */
public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    List<Post> postList;

    private FirebaseFirestore db;

    private String currentUid;

    public AdapterPosts(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //inflate layout row_post.xml
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RowPostsBinding binding = RowPostsBinding.inflate(layoutInflater, parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {

        private RowPostsBinding binding;

        public MyHolder(RowPostsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Post post) {
            binding.setPost(post);
            binding.executePendingBindings();

            // Retrieve the post document
            db = FirebaseFirestore.getInstance();
            DocumentReference postRef = db.collection("posts").document(post.getPostID());
            postRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot postDocument = task.getResult();

                    // Check whether the post has "likedByUsers" field
                    if (!postDocument.contains("likedByUsers")) {
                        postRef.update("likedByUsers", new ArrayList<String>())
                                .addOnSuccessListener(aVoid -> {
                                    // Field "likedByUsers" added successfully

                                })
                                .addOnFailureListener(e -> {
                                    // Error adding field "likedByUsers"
                                });
                    }

                    // If likedByUsers exists, check whether this post is liked by current user
                    if (postDocument.contains("likedByUsers")) {
                        List<String> likedByUsers = (List<String>) postDocument.get("likedByUsers");
                        currentUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                        // Display like states of the like button
                        if (!likedByUsers.contains(currentUid)) {
                            binding.likePostBtn.setImageResource(R.drawable.like_icon);
                        } else {
                            binding.likePostBtn.setImageResource(R.drawable.liked_icon);
                        }

                        // If the user presses the like button, the post is liked or disliked
                        binding.likePostBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!likedByUsers.contains(currentUid)) {
                                    likedByUsers.add(currentUid);
                                    binding.likePostBtn.setImageResource(R.drawable.liked_icon);
                                    post.incrementLikeNumber();
                                    binding.pLikesTv.setText(String.valueOf(post.getLikeNumber()));
                                } else {
                                    binding.likePostBtn.setImageResource(R.drawable.like_icon);
                                    likedByUsers.remove(currentUid);
                                    post.decrementLikeNumber();
                                    binding.pLikesTv.setText(String.valueOf(post.getLikeNumber()));
                                }
                                postRef.update("likedByUsers", likedByUsers);
                                postRef.update("likes", post.getLikeNumber());
                            }
                        });
                    }

                } else {
                    // Document not exist
                }
            });

        }

    }
}
