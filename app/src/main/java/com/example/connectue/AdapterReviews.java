package com.example.connectue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;

import java.util.ArrayList;

public class AdapterReviews extends RecyclerView.Adapter<AdapterReviews.MyViewHolder> {

    Context context; // for inflation
    ArrayList<Review> reviewModels;

    public AdapterReviews(Context context, ArrayList<Review> reviewModels) {
        this.context = context;
        this.reviewModels = reviewModels;
    }

    @NonNull
    @Override
    public AdapterReviews.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where we inflate the layout (Giving a look to our rows)

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.course_review_row, parent, false);
        return new AdapterReviews.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterReviews.MyViewHolder holder, int position) {
        // assign values to the views we created in the course_review_row file
        // based on the position of the recycler view

        holder.uName.setText(reviewModels.get(position).getuName());
        holder.review.setText(reviewModels.get(position).getuText());
        holder.star.setImageResource(reviewModels.get(position).getStars());
        holder.like.setImageResource(reviewModels.get(position).getpLikes());
        holder.dislike.setImageResource(reviewModels.get(position).getpDislikes());
        holder.likeNum.setText(reviewModels.get(position).getLikeNum());
        holder.dislikeNum.setText(reviewModels.get(position).getDislikeNum());
    }

    @Override
    public int getItemCount() {
        // the recycler view just wants to know the number of items you want to display
        return reviewModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        // grab the views from course_review_row file

        ImageView star, like, dislike;
        TextView uName, review;
        TextView likeNum, dislikeNum;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            star = itemView.findViewById(R.id.star);
            like = itemView.findViewById(R.id.likePostBtn);
            dislike = itemView.findViewById(R.id.dislikeReview);
            uName = itemView.findViewById(R.id.uName);
            review = itemView.findViewById(R.id.review);
            likeNum = itemView.findViewById(R.id.likeNum);
            dislikeNum = itemView.findViewById(R.id.dislikeNum);
        }
    }
}

