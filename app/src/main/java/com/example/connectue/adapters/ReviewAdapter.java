package com.example.connectue.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;

import com.example.connectue.R;
import com.example.connectue.managers.UserManager;
import com.example.connectue.interfaces.FireStoreDownloadCallback;
import com.example.connectue.model.Review;
import com.example.connectue.model.User2;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    Context context; // for inflation
    List<Review> reviewModels;

    private String TAG = "ReviewAdapter: ";

    public ReviewAdapter(Context context, List<Review> reviewModels) {
        this.context = context;
        this.reviewModels = reviewModels;
    }

    @NonNull
    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where we inflate the layout (Giving a look to our rows)

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.course_review_row, parent, false);
        return new ReviewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.MyViewHolder holder, int position) {
        // assign values to the views we created in the course_review_row file
        // based on the position of the recycler view
        Review review = reviewModels.get(position);


        holder.userManager.downloadOne(review.getPublisherId(),
                new FireStoreDownloadCallback<User2>() {
                    @Override
                    public void onSuccess(User2 user) {
                        holder.uName.setText(user.getFirstName());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Error getting the user", e);
                    }});
        holder.review.setText(reviewModels.get(position).getText());
        holder.date.setText(reviewModels.get(position).getDatetime().toString());
        holder.star.setImageResource(R.drawable.star);
        holder.like.setImageResource(R.drawable.like_icon);
        holder.dislike.setImageResource(R.drawable.dislike);
        holder.likeNum.setText(String.valueOf(reviewModels.get(position).getLikeNumber()));
        holder.dislikeNum.setText(String.valueOf(reviewModels.get(position).getDislikeNumber()));
    }

    @Override
    public int getItemCount() {
        // the recycler view just wants to know the number of items you want to display
        return reviewModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        // grab the views from course_review_row file

        ImageView star, like, dislike;
        TextView uName, review, date;
        TextView likeNum, dislikeNum;

        UserManager userManager;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            star = itemView.findViewById(R.id.star);
            date = itemView.findViewById(R.id.date);
            like = itemView.findViewById(R.id.likePostBtn);
            dislike = itemView.findViewById(R.id.dislikeReview);
            uName = itemView.findViewById(R.id.uName);
            review = itemView.findViewById(R.id.review);
            likeNum = itemView.findViewById(R.id.likeNum);
            dislikeNum = itemView.findViewById(R.id.dislikeNum);

            userManager = new UserManager(FirebaseFirestore.getInstance(), "users");
        }
    }
}

