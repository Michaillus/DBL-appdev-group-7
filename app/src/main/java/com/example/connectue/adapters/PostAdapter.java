package com.example.connectue.adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.connectue.interfaces.ItemLikeCallback;
import com.example.connectue.managers.PostManager;
import com.example.connectue.utils.General;
import com.example.connectue.fragments.PostFragment;
import com.example.connectue.R;
import com.example.connectue.databinding.RowPostsBinding;
import com.example.connectue.interfaces.ItemDownloadCallback;
import com.example.connectue.managers.UserManager;
import com.example.connectue.model.Post;
import com.example.connectue.model.User;
import com.example.connectue.utils.TimeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

/**
 * Class for using recyclerView to display posts.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyHolder> {

    private static final String TAG = "AdapterPosts";

    List<Post> postList;

    private String currentUid;
    private FragmentManager fragmentManager;

    public PostAdapter(List<Post> postList, FragmentManager fragmentManager) {
        this.postList = postList;
        this.fragmentManager = fragmentManager;
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
    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RowPostsBinding binding;

        ImageView pImage;

        TextView publisherName, description, likeNumber, commentNumber, publishTime;

        UserManager userManager;
        PostManager postManager;

        public MyHolder(RowPostsBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            binding.postCardView.setOnClickListener(this);

            publisherName = itemView.findViewById(R.id.uNamePost);
            description = itemView.findViewById(R.id.pDescriptionTv);
            likeNumber = itemView.findViewById(R.id.pLikesTv);
            commentNumber = itemView.findViewById(R.id.pCommentTv);
            publishTime = itemView.findViewById(R.id.PublishTimePost);

            userManager = new UserManager(FirebaseFirestore.getInstance(), "users");

            postManager = new PostManager(FirebaseFirestore.getInstance(),
                    Post.POST_COLLECTION_NAME, Post.POST_LIKE_COLLECTION_NAME,
                    Post.POST_DISLIKE_COLLECTION_NAME, Post.POST_COMMENT_COLLECTION_NAME);
        }

        public void bind(Post post) {

            // Set publisher name
            userManager.downloadOne(post.getPublisherId(),
                new ItemDownloadCallback<User>() {
                    @Override
                    public void onSuccess(User user) {
                        publisherName.setText(user.getFullName());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Error getting the user", e);
                    }});

            // Set post text
            description.setText(post.getText());
            // Set like number
            likeNumber.setText(String.valueOf(post.getLikeNumber()));
            // Set comment number
            commentNumber.setText(String.valueOf(post.getCommentNumber()));
            // Set publish time
            publishTime.setText(TimeUtils.getTimeAgo(post.getDatetime()));

            binding.setPost(post);
            binding.executePendingBindings();

            currentUid = Objects.requireNonNull(FirebaseAuth.getInstance()
                    .getCurrentUser()).getUid();

            // Display like button depending on whether the post is liked
            postManager.isLiked(post.getId(), currentUid, new ItemLikeCallback() {
                @Override
                public void onSuccess(Boolean isLiked) {
                    if (!isLiked) {
                        binding.likePostBtn.setImageResource(R.drawable.like_icon);
                    } else {
                        binding.likePostBtn.setImageResource(R.drawable.liked_icon);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                }
            });

            // When the user click the like button
            binding.likePostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postManager.likeOrUnlike(post, currentUid, new ItemLikeCallback() {
                        @Override
                        public void onSuccess(Boolean isLiked) {
                            if (!isLiked) {
                                binding.likePostBtn.setImageResource(R.drawable.like_icon);
                            } else {
                                binding.likePostBtn.setImageResource(R.drawable.liked_icon);
                            }
                            binding.pLikesTv.setText(String.valueOf(post.getLikeNumber()));
                        }

                        @Override
                        public void onFailure(Exception e) {}
                    });
                }
            });

            binding.reportBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    General.reportOperation(itemView.getContext(), General.POSTCOLLECTION, post.getId());
                }
            });

            // Click a comment button to jump to post details page
            binding.commentPostBtn.setOnClickListener(v -> {
                Log.d(TAG, "onClick: card clicked");
                navigateToPostFragment(post.getId());
            });
        }

        // Click a post card to jump to post details page
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: card clicked");
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String postId = postList.get(position).getId();
                navigateToPostFragment(postId);
            }
        }

        private void navigateToPostFragment(String postId) {
            PostFragment postFragment = new PostFragment();
            Bundle bundle = new Bundle();
            bundle.putString("postId", postId);
            postFragment.setArguments(bundle);

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frame_layout, postFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
