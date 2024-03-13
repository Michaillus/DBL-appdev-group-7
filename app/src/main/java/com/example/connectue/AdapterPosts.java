package com.example.connectue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;

import com.example.connectue.databinding.RowPostsBinding;

import java.util.List;

/**
 * Class for using recyclerView to display posts.
 */
public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    List<Post> postList;

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
            binding.likePostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (post.isLiked()) {
                        binding.likePostBtn.setImageResource(R.drawable.liked_icon);

                    } else {
                        binding.likePostBtn.setImageResource(R.drawable.like_icon);
                    }
                }
            });
        }

    }
}
